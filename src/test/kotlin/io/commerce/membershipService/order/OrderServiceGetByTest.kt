package io.commerce.membershipService.order

import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.fixture.order
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing

@DataMongoTest
@EnableReactiveMongoAuditing
@Import(OrderService::class)
class OrderServiceGetByTest(
    private val orderRepository: OrderRepository,
    private val orderService: OrderService
) : BehaviorSpec({

    Given("getBy(number: String, customerId: String): Order") {

        When("주문서가 존재하지 않는 경우") {
            afterEach {
                orderRepository.deleteAll()
            }

            Then("OrderError.ORDER_NOT_FOUND") {
                val result = shouldThrow<ErrorCodeException> { orderService.getBy("unknown", "unknown") }
                result.errorCode shouldBe OrderError.ORDER_NOT_FOUND.code
                result.reason shouldBe OrderError.ORDER_NOT_FOUND.message
            }
        }

        When("주문서가 있는 경우") {
            val customerId = faker.random.nextUUID()
            val orderNumber = "HF-123131313123"
            beforeEach {
                val order = order {
                    this.number = orderNumber
                    this.customerId = customerId
                }
                orderRepository.save(order)
            }

            afterEach {
                orderRepository.deleteAll()
            }

            Then("주문서 1건이 조회된다.") {
                val result = orderService.getBy(orderNumber, customerId)
                result.id shouldNotBe null
                result.customerId shouldBe customerId
                result.number shouldBe orderNumber
            }
        }
    }
})
