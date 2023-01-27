package io.commerce.membershipService.membership.policy

import io.commerce.membershipService.fixture.MembershipPolicyFixture
import io.commerce.membershipService.membership.MembershipType
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.collect
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest

@DataMongoTest
@MembershipPolicyServiceTest
class MembershipPolicyServiceGetAllViewTest(
    private val membershipPolicyRepository: MembershipPolicyRepository,
    private val membershipPolicyService: MembershipPolicyService
) : BehaviorSpec({

    Given("회원 등급 정책 조회 성공 하는 경우") {
        beforeTest {
            membershipPolicyRepository.saveAll(MembershipPolicyFixture.membershipPolicies).collect()
        }

        afterTest {
            membershipPolicyRepository.deleteAll()
        }

        Then("level: 1 , name: '메이트' 회원 등급이 1개 존재한다.") {
            val result = membershipPolicyService.getAllViews()
            result.forOne { policy ->
                policy.name shouldBe MembershipType.MATE.label
                policy.level shouldBe 1
            }
        }

        Then("level: 2 , name: '화이트' 회원 등급이 1개 존재한다.") {
            val result = membershipPolicyService.getAllViews()
            result.forOne { policy ->
                policy.name shouldBe MembershipType.WHITE.label
                policy.level shouldBe 2
            }
        }

        Then("level: 3 , name: '코랄' 회원 등급이 1개 존재한다.") {
            val result = membershipPolicyService.getAllViews()
            result.forOne { policy ->
                policy.name shouldBe MembershipType.CORAL.label
                policy.level shouldBe 3
            }
        }

        Then("level: 4 , name: '레드' 회원 등급이 1개 존재한다.") {
            val result = membershipPolicyService.getAllViews()
            result.forOne { policy ->
                policy.name shouldBe MembershipType.RED.label
                policy.level shouldBe 4
            }
        }
    }
})
