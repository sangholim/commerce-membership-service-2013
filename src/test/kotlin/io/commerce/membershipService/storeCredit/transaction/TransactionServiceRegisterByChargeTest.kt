package io.commerce.membershipService.storeCredit.transaction

import io.commerce.membershipService.fixture.chargeStoreCreditPayload
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
class TransactionServiceRegisterByChargeTest(
    private val transactionRepository: TransactionRepository,
    private val transactionService: TransactionService
) : DescribeSpec({
    val amount = 5_000
    val customerId = faker.random.nextUUID()
    val payload = chargeStoreCreditPayload {
        this.orderId = ObjectId.get()
        this.amount = amount
    }

    describe("suspend fun registerByCharge(customerId: String, amount: Int): Transaction") {
        context("적립금 거래 내역 (사용)을 생성한 경우") {
            afterEach {
                transactionRepository.deleteAll()
            }

            it("적립금 거래 내역 id는 존재한다") {
                val result = transactionService.registerByCharge(customerId, payload)
                result.id shouldNotBe null
            }

            it("적립금 거래 내역 타입은 CHARGE 이다") {
                val result = transactionService.registerByCharge(customerId, payload)
                result.type shouldBe TransactionType.CHARGE
            }

            it("적립금 거래 내역 금액은 5_000 이다") {
                val result = transactionService.registerByCharge(customerId, payload)
                result.amount shouldBe amount
            }

            it("적립금 거래 내역 생성일은 존재한다") {
                val result = transactionService.registerByCharge(customerId, payload)
                result.createdAt shouldNotBe null
            }

        }
    }
})
