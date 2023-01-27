package io.commerce.membershipService.membership

import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.fixture.MembershipPolicyFixture
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.membership.policy.MembershipPolicyRepository
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest

@DataMongoTest
@MembershipServiceTest
class MembershipServiceRegisterByTest(
    private val membershipPolicyRepository: MembershipPolicyRepository,
    private val membershipRepository: MembershipRepository,
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val membershipService: MembershipService
) : BehaviorSpec({
    val customerId = faker.random.nextUUID()

    Given("회원 등급 생성") {
        When("사용중인 회원 등급이 존재하는 경우") {
            beforeTest {
                membershipPolicyRepository.saveAll(MembershipPolicyFixture.membershipPolicies).collect()
                val membership = membershipPolicyRepository.findFirstByType(MembershipType.MATE)!!
                membershipRepository.saveAll(
                    listOf(
                        Membership.ofActive(customerId, membership),
                        Membership.ofExpected(customerId, membership)
                    )
                ).collect()
            }

            afterTest {
                membershipRepository.deleteAll()
                membershipPolicyRepository.deleteAll()
                storeCreditAccountRepository.deleteAll()
            }

            Then("enum class: MembershipError.MEMBERSHIP_ALREADY_EXISTS") {
                val result = shouldThrow<ErrorCodeException> {
                    membershipService.registerBy(customerId)
                }
                result.errorCode shouldBe MembershipError.MEMBERSHIP_ALREADY_EXISTS.code
                result.reason shouldBe MembershipError.MEMBERSHIP_ALREADY_EXISTS.message
            }
        }

        When("회원 등급 생성 성공하는 경우") {
            beforeTest {
                membershipPolicyRepository.saveAll(MembershipPolicyFixture.membershipPolicies).collect()
            }

            afterTest {
                membershipRepository.deleteAll()
                membershipPolicyRepository.deleteAll()
                storeCreditAccountRepository.deleteAll()
            }

            Then("활성화 회원 등급 1개 생성된다") {
                membershipService.registerBy(customerId)
                val memberships = membershipRepository.findAll().toList()
                memberships.forExactly(1) { membership ->
                    membership.customerId shouldBe customerId
                    membership.status shouldBe MembershipStatus.ACTIVE
                    membership.type shouldBe MembershipType.MATE
                    membership.level shouldBe 1
                    membership.activeAt shouldNotBe null
                }
            }

            Then("예상 회원 등급 1개 생성된다") {
                membershipService.registerBy(customerId)
                val memberships = membershipRepository.findAll().toList()
                memberships.forExactly(1) { membership ->
                    membership.customerId shouldBe customerId
                    membership.status shouldBe MembershipStatus.EXPECTED
                    membership.type shouldBe MembershipType.MATE
                    membership.level shouldBe 1
                    membership.activeAt shouldBe null
                }
            }
        }
    }
})
