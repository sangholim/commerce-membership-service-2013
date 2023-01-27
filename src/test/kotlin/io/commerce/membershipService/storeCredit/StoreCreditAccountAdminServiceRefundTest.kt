package io.commerce.membershipService.storeCredit

import io.commerce.membershipService.storeCredit.account.StoreCreditAccountAdminService
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountError
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.fixture.order
import io.commerce.membershipService.fixture.refundPayload
import io.commerce.membershipService.fixture.storeCreditAccount
import io.commerce.membershipService.order.OrderError
import io.commerce.membershipService.order.OrderRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing

@DataMongoTest
@EnableReactiveMongoAuditing
@StoreCreditAccountAdminServiceTest
class StoreCreditAccountAdminServiceRefundTest(
    private val storeCreditAccountAdminService: StoreCreditAccountAdminService,
    private val transactionRepository: TransactionRepository,
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val orderRepository: OrderRepository
) : BehaviorSpec({
    val customerId = faker.random.nextUUID()
    val orderNumber = "HF-123131313"
    val amount = 5_000
    val note = "관리자 적립금 반환"

    Given("suspend fun refund(customerId: String, payload: RefundPayload): Unit") {
        When("주문서가 없는 경우") {
            val payload = refundPayload {
                this.orderNumber = orderNumber
            }

            afterEach {
                transactionRepository.deleteAll()
                storeCreditAccountRepository.deleteAll()
                orderRepository.deleteAll()
            }

            Then("OrderError.ORDER_NOT_FOUND") {
                val result =
                    shouldThrow<ErrorCodeException> { storeCreditAccountAdminService.refund(customerId, payload) }
                result.errorCode shouldBe OrderError.ORDER_NOT_FOUND.code
                result.reason shouldBe OrderError.ORDER_NOT_FOUND.message
            }
        }

        When("적립금 계좌가 없는 경우") {
            val payload = refundPayload {
                this.orderNumber = orderNumber
            }

            beforeEach {
                orderRepository.save(
                    order {
                        this.number = orderNumber
                        this.customerId = customerId
                    }
                )
            }

            afterEach {
                transactionRepository.deleteAll()
                storeCreditAccountRepository.deleteAll()
                orderRepository.deleteAll()
            }

            Then("StoreCreditAccountError.ACCOUNT_NOT_FOUND") {
                val result =
                    shouldThrow<ErrorCodeException> { storeCreditAccountAdminService.refund(customerId, payload) }
                result.errorCode shouldBe StoreCreditAccountError.ACCOUNT_NOT_FOUND.code
                result.reason shouldBe StoreCreditAccountError.ACCOUNT_NOT_FOUND.message
            }
        }

        When("적립금 반환 성공한 경우") {
            val payload = refundPayload {
                this.orderNumber = orderNumber
                this.amount = amount
                this.note = note
            }

            beforeEach {
                orderRepository.save(
                    order {
                        this.number = orderNumber
                        this.customerId = customerId
                    }
                )

                storeCreditAccountRepository.save(
                    storeCreditAccount {
                        this.customerId = customerId
                    }
                )
            }

            afterEach {
                transactionRepository.deleteAll()
                storeCreditAccountRepository.deleteAll()
                orderRepository.deleteAll()
            }

            Then("적립금 계좌 balance 5_000 이다") {
                storeCreditAccountAdminService.refund(customerId, payload)
                storeCreditAccountRepository.findByCustomerId(customerId)!!.should { storeCreditAccount ->
                    storeCreditAccount.customerId shouldBe customerId
                    storeCreditAccount.balance shouldBe amount
                }
            }

            Then("적립금 계좌 내에 적립금 데이터 주문 ID가 존재한다") {
                storeCreditAccountAdminService.refund(customerId, payload)
                storeCreditAccountRepository.findByCustomerId(customerId)!!.deposits
                    .forExactly(1) { storeCredit ->
                        storeCredit.orderId shouldNotBe null
                    }
            }

            Then("적립금 계좌 내에 적립금 데이터에는 amount, balance 가 5000 이다") {
                storeCreditAccountAdminService.refund(customerId, payload)
                storeCreditAccountRepository.findByCustomerId(customerId)!!.deposits
                    .forExactly(1) { storeCredit ->
                        storeCredit.amount shouldBe amount
                        storeCredit.balance shouldBe amount
                    }
            }

            Then("적립금 거래 내역이 생성된다") {
                storeCreditAccountAdminService.refund(customerId, payload)

                transactionRepository.findAll().toList().forExactly(1) { transaction ->
                    transaction.customerId shouldBe customerId
                    transaction.amount shouldBe amount
                    transaction.type shouldBe TransactionType.DEPOSIT
                }
            }
        }
    }
})
