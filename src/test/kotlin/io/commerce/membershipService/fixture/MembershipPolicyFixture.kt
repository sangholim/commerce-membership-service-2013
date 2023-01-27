package io.commerce.membershipService.fixture

import io.commerce.membershipService.membership.MembershipType
import io.commerce.membershipService.membership.policy.MembershipPolicy
import org.bson.types.ObjectId

object MembershipPolicyFixture {

    val membershipPolicies = listOf(
        MembershipPolicy(id = ObjectId.get(), 1, MembershipType.MATE, 0, 0.01),
        MembershipPolicy(id = ObjectId.get(), 2, MembershipType.WHITE, 50000, 0.02),
        MembershipPolicy(id = ObjectId.get(), 3, MembershipType.CORAL, 100000, 0.03),
        MembershipPolicy(id = ObjectId.get(), 4, MembershipType.RED, 200000, 0.05)
    )
}
