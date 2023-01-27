package io.commerce.membershipService.membership

import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class MembershipCrudService(
    private val membershipRepository: MembershipRepository
) {
    /**
     * 회원등급 리스트 생성
     *
     * @param memberships 회원 등급 리스트 (활성화 회원 등급, 예상 회원 등급)
     */
    suspend fun registerBy(memberships: List<Membership>): List<Membership> =
        membershipRepository.saveAll(memberships).toList()

    /**
     * 회원 등급 존재 여부
     *
     * @param customerId 고객 ID
     */
    suspend fun existByCustomerId(customerId: String): Boolean =
        membershipRepository.existsByCustomerIdAndStatusIs(customerId, MembershipStatus.ACTIVE)

    /**
     * 회원 등급 조회
     * @param customerId 고객 ID
     */
    suspend fun getBy(customerId: String): Membership? =
        membershipRepository.findByCustomerIdAndStatusIs(customerId, MembershipStatus.ACTIVE)
}
