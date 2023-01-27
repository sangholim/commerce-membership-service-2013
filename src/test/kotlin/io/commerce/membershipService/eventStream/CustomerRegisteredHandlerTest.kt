package io.commerce.membershipService.eventStream

import io.commerce.membershipService.eventStream.customerRegistered.CustomerRegisteredPayload
import io.commerce.membershipService.fixture.MembershipPolicyFixture
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.membership.MembershipRepository
import io.commerce.membershipService.membership.MembershipStatus
import io.commerce.membershipService.membership.MembershipType
import io.commerce.membershipService.membership.policy.MembershipPolicyRepository
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.messaging.support.MessageBuilder
import java.util.*
import kotlin.time.Duration.Companion.seconds

@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
class CustomerRegisteredHandlerTest(
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val membershipRepository: MembershipRepository,
    private val membershipPolicyRepository: MembershipPolicyRepository,
    private val inputDestination: InputDestination
) : BehaviorSpec({
    val destination = "customer-registered"

    beforeSpec {
        membershipPolicyRepository.saveAll(MembershipPolicyFixture.membershipPolicies).collect()
    }

    Given("customerRegisteredConsumer()") {
        When("이벤트 처리 성공한 경우") {
            val payload = CustomerRegisteredPayload(faker.random.nextUUID())
            val message = MessageBuilder
                .withPayload(payload)
                .build()

            beforeTest {
                storeCreditAccountRepository.deleteAll()
                membershipRepository.deleteAll()
                inputDestination.send(message, destination)
            }

            Then("활성화 회원 등급, 예상 회원 등급 1개씩 생성") {
                eventually(3.seconds) {
                    val result = membershipRepository.findAll().toList()

                    result.forExactly(1) { membership ->
                        membership.customerId shouldBe payload.customerId
                        membership.status shouldBe MembershipStatus.ACTIVE
                        membership.type shouldBe MembershipType.MATE
                        membership.level shouldBe 1
                        membership.activeAt shouldNotBe null
                    }

                    result.forExactly(1) { membership ->
                        membership.customerId shouldBe payload.customerId
                        membership.status shouldBe MembershipStatus.EXPECTED
                        membership.type shouldBe MembershipType.MATE
                        membership.level shouldBe 1
                        membership.activeAt shouldBe null
                    }
                }
            }

            Then("고객의 적립금 계좌 개설") {
                eventually(3.seconds) {
                    storeCreditAccountRepository.findByCustomerId(payload.customerId)
                        .shouldNotBeNull()
                        .should {
                            it.shouldNotBeNull()
                            it.balance shouldBe 0
                            it.amountToExpire shouldBe 0
                            it.deposits.shouldBeEmpty()
                        }
                }
            }
        }
    }
})
