package io.commerce.membershipService.membership

import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface MembershipMapper {
    @Mapping(target = "name", source = "type.label")
    fun toMembershipView(membership: Membership): MembershipView
}
