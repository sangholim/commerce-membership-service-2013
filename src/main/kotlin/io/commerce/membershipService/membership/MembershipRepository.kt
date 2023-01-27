package io.commerce.membershipService.membership

import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface MembershipRepository : CoroutineSortingRepository<Membership, ObjectId> {
    suspend fun existsByCustomerIdAndStatusIs(customerId: String, status: MembershipStatus): Boolean

    suspend fun findByCustomerIdAndStatusIs(customerId: String, status: MembershipStatus): Membership?
}
