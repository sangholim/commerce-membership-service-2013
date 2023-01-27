package io.commerce.membershipService.membership.policy

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 회원 등급 정책 응답 데이터
 */
data class MembershipPolicyView(
    /**
     * 등급 레벨 1-4
     */
    val level: Int,

    /**
     * 등급명
     */
    val name: String,

    /**
     * 최소 누적 실적금액
     */
    val minimumCredit: Int,

    /**
     * 최대 누적 실적금액
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val maximumCredit: Int?,

    /**
     * 구매 확정(실적금액)시 적립율
     */
    val creditRewardRate: Double
)
