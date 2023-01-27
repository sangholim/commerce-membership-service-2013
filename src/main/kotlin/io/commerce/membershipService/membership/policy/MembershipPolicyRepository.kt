package io.commerce.membershipService.membership.policy

import io.commerce.membershipService.membership.MembershipType
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface MembershipPolicyRepository : CoroutineSortingRepository<MembershipPolicy, ObjectId> {
    suspend fun findFirstByType(type: MembershipType): MembershipPolicy?

    fun findAllByOrderByLevelAsc(): Flow<MembershipPolicy>
}
