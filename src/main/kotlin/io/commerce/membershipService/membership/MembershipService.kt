package io.commerce.membershipService.membership

import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.membership.policy.MembershipPolicyService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 회원 등급 통합 서비스
 */
@Service
class MembershipService(
    private val membershipMapper: MembershipMapper,
    private val membershipCrudService: MembershipCrudService,
    private val membershipPolicyService: MembershipPolicyService
) {

    /**
     * 회원 등급 조회
     * 없는 경우 회원 등급 생성
     * @param customerId 고객 ID
     */
    suspend fun getOrRegisterBy(customerId: String): MembershipView {
        val membership = membershipCrudService.getBy(customerId)
            ?: registerBy(customerId).first { it.status == MembershipStatus.ACTIVE }
        return membershipMapper.toMembershipView(membership)
    }

    /**
     * 신규 회원 등급 생성 - ACTIVE, EXPECTED
     * @param customerId 고객 ID
     */
    @Transactional
    suspend fun registerBy(customerId: String): List<Membership> {
        if (membershipCrudService.existByCustomerId(customerId)) {
            throw ErrorCodeException.of(MembershipError.MEMBERSHIP_ALREADY_EXISTS)
        }

        val membershipPolicy = membershipPolicyService.getByType(MembershipType.MATE)
        return membershipCrudService.registerBy(
            listOf(
                Membership.ofActive(customerId, membershipPolicy),
                Membership.ofExpected(customerId, membershipPolicy)
            )
        )
    }
}
