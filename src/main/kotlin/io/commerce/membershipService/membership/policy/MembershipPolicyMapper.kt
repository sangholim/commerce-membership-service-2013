package io.commerce.membershipService.membership.policy

import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface MembershipPolicyMapper {

    @Mapping(target = "name", source = "membershipPolicy.type.label")
    fun toMembershipPolicyView(membershipPolicy: MembershipPolicy, maximumCredit: Int?): MembershipPolicyView
}
