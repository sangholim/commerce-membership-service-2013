package io.commerce.membershipService.storeCredit

import io.commerce.membershipService.storeCredit.account.StoreCreditAccountAdminService
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountError
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.fixture.depositPayload
import io.commerce.membershipService.fixture.storeCredit
import io.commerce.membershipService.fixture.storeCreditAccount
import io.commerce.membershipService.storeCredit.transaction.TransactionRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import java.time.ZonedDateTime

@DataMongoTest
@EnableReactiveMongoAuditing
@StoreCreditAccountAdminServiceTest
class StoreCreditAccountAdminServiceDepositTest(
    private val storeCreditAccountAdminService: StoreCreditAccountAdminService,
    private val transactionRepository: TransactionRepository,
    private val storeCreditAccountRepository: StoreCreditAccountRepository
) : DescribeSpec({
    describe("deposit(customerId: String,payload: DepositPayload): Unit") {
        context("고객의 적립금 계좌가 존재하지 않을때") {
            val payload = depositPayload()
            it("StoreCreditAccountError.ACCOUNT_NOT_FOUND") {
                val exception = shouldThrowExactly<ErrorCodeException> {
                    storeCreditAccountAdminService.deposit("DOES NOT EXIST", payload)
                }
                exception.errorCode shouldBe StoreCreditAccountError.ACCOUNT_NOT_FOUND.code
            }
        }

        context("고객의 적립금 계좌가 존재할때") {
            val pageable = PageRequest.of(0, 25)
            val createdAt = ZonedDateTime.now().minusMonths(11).toInstant()
            val account = storeCreditAccount {
                deposits = List(2) {
                    storeCredit {
                        amount = 5_000
                    }
                }
            }
            val note = "적립금 지급"
            val payload = depositPayload {
                this.amount = 10_000
                this.note = note
            }

            beforeEach {
                storeCreditAccountRepository.save(account)
            }

            afterEach {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
            }

            it("적립금 계좌 내에 적립금 데이터 리스트 내에서는 주문 ID는 존재하지 않는다") {
                storeCreditAccountAdminService.deposit(account.customerId, payload)
                storeCreditAccountRepository.findAll().toList()
                    .flatMap { it.deposits }
                    .forNone { storeCredit ->
                        storeCredit.orderId shouldNotBe null
                    }
            }

            it("적립금 거래 내역 (적립)은 데이터가 1개이다") {
                storeCreditAccountAdminService.deposit(account.customerId, payload)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.DEPOSIT,
                    createdAt,
                    pageable
                ).count() shouldBe 1
            }

            it("적립금 거래 내역 (적립)은 금액이 10_000") {
                storeCreditAccountAdminService.deposit(account.customerId, payload)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.DEPOSIT,
                    createdAt,
                    pageable
                ).toList().forOne { transactionView ->
                    transactionView.amount shouldBe payload.amount
                }
            }

            it("적립금 거래 내역 (적립)은 내용은 '적립금 지급'이다") {
                storeCreditAccountAdminService.deposit(account.customerId, payload)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.DEPOSIT,
                    createdAt,
                    pageable
                ).toList().forOne { transactionView ->
                    transactionView.note shouldBe note
                }
            }

            it("적립금 거래 내역 (적립)은 주문 ID는 존재하지 않는다") {
                storeCreditAccountAdminService.deposit(account.customerId, payload)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.DEPOSIT,
                    createdAt,
                    pageable
                ).toList().forExactly(1) { transactionView ->
                    transactionView.orderId shouldBe null
                }
            }
        }
    }
})
