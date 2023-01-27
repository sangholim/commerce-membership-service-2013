package io.commerce.membershipService.membership.policy

import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.fixture.MembershipPolicyFixture
import io.commerce.membershipService.membership.MembershipType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.collect
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest

@DataMongoTest
@MembershipPolicyServiceTest
class MembershipPolicyServiceGetByNameTest(
    private val membershipPolicyRepository: MembershipPolicyRepository,
    private val membershipPolicyService: MembershipPolicyService
) : BehaviorSpec({

    Given("회원 등급명으로 회원 등급 정책 조회하기") {
        When("회원 등급 정책이 존재하지 않는 경우") {
            beforeTest {
                membershipPolicyRepository.saveAll(
                    MembershipPolicyFixture.membershipPolicies.filter { it.type != MembershipType.MATE }
                ).collect()
            }

            afterTest {
                membershipPolicyRepository.deleteAll()
            }

            Then("enum class: MembershipPolicyError.MEMBERSHIP_POLICY_NOT_FOUND") {
                val result = shouldThrow<ErrorCodeException> {
                    membershipPolicyService.getByType(MembershipType.MATE)
                }
                result.errorCode shouldBe MembershipPolicyError.MEMBERSHIP_POLICY_NOT_FOUND.code
                result.reason shouldBe MembershipPolicyError.MEMBERSHIP_POLICY_NOT_FOUND.message
            }
        }

        When("회원 등급 정책 조회 성공한 경우") {
            beforeTest {
                membershipPolicyRepository.saveAll(
                    MembershipPolicyFixture.membershipPolicies
                ).collect()
            }

            afterTest {
                membershipPolicyRepository.deleteAll()
            }

            Then("name: MATE, level: 1 인 회원 정책이 조회된다") {
                val result = membershipPolicyService.getByType(MembershipType.MATE)
                result.type shouldBe MembershipType.MATE
                result.level shouldBe 1
            }
        }
    }
})
