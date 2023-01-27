package io.commerce.membershipService.storeCredit

import io.commerce.membershipService.storeCredit.transaction.TransactionNotes
import io.commerce.membershipService.storeCredit.transaction.TransactionRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.fixture.chargeStoreCreditPayload
import io.commerce.membershipService.fixture.storeCredit
import io.commerce.membershipService.fixture.storeCreditAccount
import io.commerce.membershipService.fixture.transaction
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountError
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountService
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import java.time.ZonedDateTime

@DataMongoTest
@EnableReactiveMongoAuditing
@StoreCreditAccountServiceTest
class StoreCreditAccountServiceChargeTest(
    private val transactionRepository: TransactionRepository,
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val storeCreditAccountService: StoreCreditAccountService
) : DescribeSpec({
    val orderId = ObjectId.get()

    context("고객의 적립금 계좌가 존재하지 않을때") {
        it("Return null") {
            val exception = shouldThrowExactly<ErrorCodeException> {
                storeCreditAccountService.charge("customerId", chargeStoreCreditPayload())
            }

            exception.errorCode shouldBe StoreCreditAccountError.ACCOUNT_NOT_FOUND.code
        }
    }

    describe("고객의 적립금 계좌가 존재할때") {
        context("적립금 계좌의 잔액이 부족할때") {
            val account = storeCreditAccount()

            beforeEach {
                storeCreditAccountRepository.save(account)
            }

            afterEach {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
            }

            it("StoreCreditAccountError.INSUFFICIENT_BALANCE") {
                val exception = shouldThrowExactly<ErrorCodeException> {
                    storeCreditAccountService.charge(
                        account.customerId,
                        chargeStoreCreditPayload {
                            this.orderId = orderId
                            this.amount = Int.MAX_VALUE
                        }
                    )
                }

                exception.errorCode shouldBe StoreCreditAccountError.INSUFFICIENT_BALANCE.code
            }
        }

        context("적립금 계좌 잔액을 사용할때") {
            val year = 1L
            val payload = chargeStoreCreditPayload {
                this.orderId = orderId
                this.amount = 25_000
            }
            val deposits = listOf(
                storeCredit {
                    val date = ZonedDateTime.now().minusMonths(12)
                    amount = 11_000
                    issuedAt = date.toInstant()
                    expiry = date.plusYears(year).toInstant()
                },
                storeCredit {
                    val date = ZonedDateTime.now().minusMonths(11)
                    amount = 10_000
                    issuedAt = date.toInstant()
                    expiry = date.plusYears(year).toInstant()
                },
                storeCredit {
                    val date = ZonedDateTime.now().minusMonths(10)
                    amount = 9_000
                    issuedAt = date.toInstant()
                    expiry = date.plusYears(year).toInstant()
                },
                storeCredit {
                    val date = ZonedDateTime.now().minusMonths(9)
                    amount = 8_000
                    issuedAt = date.toInstant()
                    expiry = date.plusYears(year).toInstant()
                },
                storeCredit {
                    val date = ZonedDateTime.now().minusMonths(8)
                    amount = 7_000
                    issuedAt = date.toInstant()
                    expiry = date.plusYears(year).toInstant()
                }
            )

            val account = storeCreditAccount {
                this.deposits = deposits
            }
            val pageable = PageRequest.of(0, 25)
            val createdAt = ZonedDateTime.now().minusMonths(11).toInstant()

            beforeEach {
                storeCreditAccountRepository.save(account)
                transactionRepository.save(
                    transaction {
                        customerId = account.customerId
                        amount = account.balance
                    }
                )
            }

            afterEach {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
            }

            it("적립금 계좌 소멸 예정 금액은 0 이다") {
                storeCreditAccountService.charge(account.customerId, payload)
                val result = storeCreditAccountRepository.findByCustomerId(account.customerId)!!
                result.amountToExpire shouldBe 0
            }

            it("적립금 계좌 잔고는 20_000 이다") {
                storeCreditAccountService.charge(account.customerId, payload)
                val result = storeCreditAccountRepository.findByCustomerId(account.customerId)!!
                result.balance shouldBe 20_000
            }

            it("적립금 데이터 리스트 3개이다") {
                storeCreditAccountService.charge(account.customerId, payload)
                val result = storeCreditAccountRepository.findByCustomerId(account.customerId)!!
                result.deposits.size shouldBe 3
            }

            it("적립금 데이터 리스트 내에서 총 금액이 9000원이고 잔고가 5000원인 데이터가 한개 존재한다") {
                storeCreditAccountService.charge(account.customerId, payload)
                val result = storeCreditAccountRepository.findByCustomerId(account.customerId)!!
                result.deposits.forOne {
                    it.amount shouldBe 9000
                    it.balance shouldBe 5000
                }
            }

            it("적립금 데이터 리스트 내에서 총 금액과 남은 금액이동일한 데이터가 두개 존재한다") {
                storeCreditAccountService.charge(account.customerId, payload)
                val result = storeCreditAccountRepository.findByCustomerId(account.customerId)!!
                result.deposits.forExactly(2) {
                    it.amount shouldBe it.balance
                }
            }

            it("적립금 거래 내역 (사용)은 데이터가 1개이다") {
                storeCreditAccountService.charge(account.customerId, payload)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.CHARGE,
                    createdAt,
                    pageable
                )
                    .count() shouldBe 1
            }

            it("적립금 거래 내역 (사용)은 금액이 25_000이다") {
                storeCreditAccountService.charge(account.customerId, payload)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.CHARGE,
                    createdAt,
                    pageable
                )
                    .toList().forOne { transactionView ->
                        transactionView.amount shouldBe payload.amount
                    }
            }

            it("적립금 거래 내역 (사용)은 내용은 '구매 사용'이다") {
                storeCreditAccountService.charge(account.customerId, payload)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.CHARGE,
                    createdAt,
                    pageable
                )
                    .toList().forOne { transactionView ->
                        transactionView.note shouldBe TransactionNotes.PURCHASE_CHARGE
                    }
            }
        }
    }
})
