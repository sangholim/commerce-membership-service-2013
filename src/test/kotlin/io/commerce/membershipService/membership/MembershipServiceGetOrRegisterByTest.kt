package io.commerce.membershipService.membership

import io.commerce.membershipService.fixture.MembershipPolicyFixture
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.membership.policy.MembershipPolicyRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.collect
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest

@DataMongoTest
@MembershipServiceTest
class MembershipServiceGetOrRegisterByTest(
    private val membershipService: MembershipService,
    private val membershipCrudService: MembershipCrudService,
    private val membershipRepository: MembershipRepository,
    private val membershipPolicyRepository: MembershipPolicyRepository
) : BehaviorSpec({
    val customerId = faker.random.nextUUID()

    Given("회원 등급 조회 또는 생성하기") {
        When("회원 등급이 없는 경우") {
            beforeEach {
                membershipPolicyRepository.saveAll(
                    MembershipPolicyFixture.membershipPolicies
                ).collect()
            }
            afterEach {
                membershipRepository.deleteAll()
                membershipPolicyRepository.deleteAll()
            }
            Then("새로운 회원 등급이 생성된다") {
                membershipService.getOrRegisterBy(customerId)
                membershipCrudService.getBy(customerId)
                    .shouldNotBeNull()
                    .should { membership ->
                        membership.id shouldNotBe null
                        membership.type shouldBe MembershipType.MATE
                        membership.level shouldBe 1
                    }
            }
        }

        When("회원 등급이 존재하는 경우") {
            lateinit var membership: Membership
            beforeEach {
                membershipPolicyRepository.saveAll(
                    MembershipPolicyFixture.membershipPolicies
                ).collect()
                membership = membershipService.registerBy(customerId).first { it.status == MembershipStatus.ACTIVE }
            }
            afterEach {
                membershipRepository.deleteAll()
                membershipPolicyRepository.deleteAll()
            }
            Then("회원 등급을 조회한다") {
                membershipService.getOrRegisterBy(customerId).should {
                    it.name shouldBe membership.type.label
                    it.creditRewardRate shouldBe membership.creditRewardRate
                }
            }
        }
    }
})
