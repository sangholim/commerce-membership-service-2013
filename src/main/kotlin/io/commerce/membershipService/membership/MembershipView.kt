package io.commerce.membershipService.membership

/**
 * 회원 등급 요약 응답 데이터
 */
data class MembershipView(
    /**
     * 등급 이름
     */
    val name: String,

    /**
     * 구매 확정(실적금액)시 적립율
     */
    val creditRewardRate: Double
)

fun Membership.toMembershipView() = MembershipView(
    name = this.type.label,
    creditRewardRate = creditRewardRate
)
