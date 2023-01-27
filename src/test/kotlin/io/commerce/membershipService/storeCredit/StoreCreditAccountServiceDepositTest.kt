package io.commerce.membershipService.storeCredit

import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.fixture.storeCredit
import io.commerce.membershipService.fixture.storeCreditAccount
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountError
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountService
import io.commerce.membershipService.storeCredit.transaction.TransactionNotes
import io.commerce.membershipService.storeCredit.transaction.TransactionRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import java.time.ZonedDateTime

@DataMongoTest
@EnableReactiveMongoAuditing
@StoreCreditAccountServiceTest
class StoreCreditAccountServiceDepositTest(
    private val transactionRepository: TransactionRepository,
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val storeCreditAccountService: StoreCreditAccountService
) : DescribeSpec({
    describe("deposit(customerId: String,storeCredit: StoreCredit, note: String): Unit") {
        context("고객의 적립금 계좌가 존재하지 않을때") {
            val storeCredit = storeCredit()

            it("StoreCreditAccountError.ACCOUNT_NOT_FOUND") {
                val exception = shouldThrowExactly<ErrorCodeException> {
                    storeCreditAccountService.deposit("DOES NOT EXIST", storeCredit, TransactionNotes.PURCHASE_DEPOSIT)
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
            val newStoreCredit = storeCredit {
                amount = 10_000
            }
            val deposits = listOf(newStoreCredit) + account.deposits

            beforeEach {
                storeCreditAccountRepository.save(account)
            }

            afterEach {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
            }

            it("새로 지급된 적립금 기준 계좌 정보 업데이트") {
                storeCreditAccountService.deposit(account.customerId, newStoreCredit, TransactionNotes.PURCHASE_DEPOSIT)
                    .shouldNotBeNull()
                    .should {
                        it.balance shouldBe deposits.sumOf(StoreCredit::balance)
                        it.amountToExpire shouldBe 0
                        it.deposits shouldBe deposits
                    }
            }

            it("적립금 거래 내역 (적립)은 데이터가 1개이다") {
                storeCreditAccountService.deposit(account.customerId, newStoreCredit, TransactionNotes.PURCHASE_DEPOSIT)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.DEPOSIT,
                    createdAt,
                    pageable
                )
                    .count() shouldBe 1
            }

            it("적립금 거래 내역 (적립)은 금액이 10_000") {
                storeCreditAccountService.deposit(account.customerId, newStoreCredit, TransactionNotes.PURCHASE_DEPOSIT)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.DEPOSIT,
                    createdAt,
                    pageable
                )
                    .toList().forOne { transactionView ->
                        transactionView.amount shouldBe newStoreCredit.amount
                    }
            }

            it("적립금 거래 내역 (적립)은 내용은 '구매 적립'이다") {
                storeCreditAccountService.deposit(account.customerId, newStoreCredit, TransactionNotes.PURCHASE_DEPOSIT)
                transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
                    account.customerId,
                    TransactionType.DEPOSIT,
                    createdAt,
                    pageable
                )
                    .toList().forOne { transactionView ->
                        transactionView.note shouldBe TransactionNotes.PURCHASE_DEPOSIT
                    }
            }
        }
    }
})
