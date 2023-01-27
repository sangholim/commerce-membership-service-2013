package io.commerce.membershipService.membership

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 회원 등급명 enum
 */
enum class MembershipType(val label: String) {
    @JsonProperty("mate")
    MATE("메이트"),

    @JsonProperty("coral")
    CORAL("코랄"),

    @JsonProperty("white")
    WHITE("화이트"),

    @JsonProperty("red")
    RED("레드")
}
