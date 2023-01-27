package io.commerce.membershipService.eventStream

import io.commerce.membershipService.fixture.*
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionNotes
import io.commerce.membershipService.storeCredit.transaction.TransactionRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.messaging.support.MessageBuilder
import kotlin.time.Duration.Companion.seconds

@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
class OrderRefundHandlerTest(
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val transactionRepository: TransactionRepository,
    private val inputDestination: InputDestination
) : BehaviorSpec({
    val destination = "order-refund"
    val amount = 5_000
    val orderId = ObjectId.get()
    val customerId = faker.random.nextUUID()

    val account = storeCreditAccount {
        this.customerId = customerId
        deposits = List(2) {
            storeCredit {
                this.amount = amount
            }
        }
    }
    val orderStoreCredit = orderStoreCredit {
        this.amount = amount
        this.transaction = orderTransactionView {
            this.customerId = customerId
            this.orderId = orderId
            this.type = TransactionType.CHARGE
            this.amount = amount
        }
    }

    Given("orderRefundConsumer()") {
        When("이벤트 처리 성공한 경우") {
            beforeTest {
                storeCreditAccountRepository.save(account)
                val payload = orderRefundPayload {
                    this.orderId = orderId
                    this.storeCredit = orderStoreCredit
                }
                val message = MessageBuilder
                    .withPayload(payload)
                    .build()
                inputDestination.send(message, destination)
            }

            afterEach {
                transactionRepository.deleteAll()
                storeCreditAccountRepository.deleteAll()
            }

            Then("업데이트 된 적립금 계좌 1개 존재한다") {
                eventually(3.seconds) {
                    val result = storeCreditAccountRepository.findAll().toList()
                    result.forOne { storeCreditAccount ->
                        storeCreditAccount.id shouldNotBe null
                        storeCreditAccount.customerId shouldBe customerId
                        storeCreditAccount.balance shouldBe 15_000
                        storeCreditAccount.deposits.count() shouldBeGreaterThan 0
                    }
                }
            }

            Then("적립된 거래내역 1건 생성된다") {
                eventually(3.seconds) {
                    val result = transactionRepository.findAll().toList()
                    result.forOne { transaction ->
                        transaction.id shouldNotBe null
                        transaction.customerId shouldBe customerId
                        transaction.amount shouldBe amount
                        transaction.type shouldBe TransactionType.DEPOSIT
                        transaction.note shouldBe TransactionNotes.STORE_CREDIT_REFUND
                        transaction.createdAt shouldNotBe null
                    }
                }
            }
        }
    }
})
