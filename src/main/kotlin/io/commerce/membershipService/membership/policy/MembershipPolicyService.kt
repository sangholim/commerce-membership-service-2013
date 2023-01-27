package io.commerce.membershipService.membership.policy

import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.membership.MembershipType
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class MembershipPolicyService(
    private val membershipPolicyRepository: MembershipPolicyRepository,
    private val membershipPolicyMapper: MembershipPolicyMapper
) {
    /**
     * 회원 등급명 기준 회원 등급 정책 조회
     * @param type 회원 등급명
     */
    suspend fun getByType(type: MembershipType): MembershipPolicy = membershipPolicyRepository.findFirstByType(type)
        ?: throw ErrorCodeException.of(MembershipPolicyError.MEMBERSHIP_POLICY_NOT_FOUND)

    /**
     * 회원 등급 정책 조회
     * 최대 누적 실적 금액 = 다음 회원 등급의 최소 누적 실적 금액
     */
    suspend fun getAllViews(): List<MembershipPolicyView> {
        val membershipPolicies = membershipPolicyRepository.findAllByOrderByLevelAsc().toList()
        return membershipPolicies.mapIndexed { index, membershipPolicy ->
            val maximumCredit = membershipPolicies.nextMembershipPolicy(index)?.minimumCredit
            membershipPolicyMapper.toMembershipPolicyView(membershipPolicy, maximumCredit)
        }
    }
}
