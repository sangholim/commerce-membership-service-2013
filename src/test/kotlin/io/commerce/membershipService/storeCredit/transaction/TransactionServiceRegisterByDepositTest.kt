package io.commerce.membershipService.storeCredit.transaction

import io.commerce.membershipService.fixture.faker
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.bson.types.ObjectId
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing

@DataMongoTest
@TransactionServiceTest
@EnableReactiveMongoAuditing
class TransactionServiceRegisterByDepositTest(
    private val transactionRepository: TransactionRepository,
    private val transactionService: TransactionService
) : DescribeSpec({
    val amount = 5_000
    val customerId = faker.random.nextUUID()
    val orderId = ObjectId.get()

    describe("suspend fun registerByDeposit(orderId: ObjectId?, customerId: String, amount: Int, note: String): Transaction") {
        context("적립금 거래 내역 (적립)을 생성한 경우") {
            afterEach {
                transactionRepository.deleteAll()
            }

            it("적립금 거래 내역 id는 존재한다") {
                val result = transactionService.registerByDeposit(orderId, customerId, amount,
                    TransactionNotes.PURCHASE_DEPOSIT
                )
                result.id shouldNotBe null
            }

            it("적립금 거래 내역 타입은 DEPOSIT 이다") {
                val result = transactionService.registerByDeposit(orderId, customerId, amount,
                    TransactionNotes.PURCHASE_DEPOSIT
                )
                result.type shouldBe TransactionType.DEPOSIT
            }

            it("적립금 거래 내역 금액은 5_000 이다") {
                val result = transactionService.registerByDeposit(orderId, customerId, amount,
                    TransactionNotes.PURCHASE_DEPOSIT
                )
                result.amount shouldBe amount
            }

            it("적립금 거래 내역 주문 ID는 존재한다") {
                val result = transactionService.registerByDeposit(orderId, customerId, amount,
                    TransactionNotes.PURCHASE_DEPOSIT
                )
                result.orderId shouldNotBe null
            }

            it("적립금 거래 내역 주문 ID는 존재하지 않는다") {
                val result = transactionService.registerByDeposit(orderId = null, customerId = customerId, amount = amount, note = TransactionNotes.PURCHASE_DEPOSIT)
                result.orderId shouldBe null
            }

            it("적립금 거래 내역 생성일은 존재한다") {
                val result = transactionService.registerByDeposit(orderId, customerId, amount,
                    TransactionNotes.PURCHASE_DEPOSIT
                )
                result.createdAt shouldNotBe null
            }
        }
    }
})
