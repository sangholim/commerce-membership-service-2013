package io.commerce.membershipService.membership

import io.commerce.membershipService.membership.event.MembershipEvent
import io.commerce.membershipService.membership.policy.MembershipPolicy
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.Instant
import javax.validation.constraints.*

/**
 * 회원등급 관리
 *
 * 매달 1일 기준 지난 3개월간 기록을 계산하여 신규 생성
 *
 * NOTE:
 * - 최초 등급 부여시 -> 1개 ACTIVE / 1개 EXPECTED Membership
 * - [MembershipEvent] 발생...
 *   - ACTIVE Membership::totalCredit 업데이트
 *   - 3개월간의 [MembershipEvent] 기록 기반
 *     - EXPECTED Membership::level 업데이트
 *     - EXPECTED Membership::name 업데이트
 * - 매월 첫달 회원등급 업데이트시
 *   - 기존 ACTIVE Membership -> EXPIRED Membership 업데이트
 *   - 기존 EXPECTED Membership -> ACTIVE Membership 업데이트
 *     - 신규 ACTIVE Membership::totalCredit 업데이트
 */
@Document
data class Membership(
    /**
     * 회원등급 ID
     */
    @MongoId
    val id: ObjectId? = null,

    /**
     * 고객 ID
     */
    @field: NotBlank
    @Indexed
    val customerId: String,

    /**
     * 회원 등급 정책 ID
     */
    val policyId: ObjectId,

    /**
     * 등급 레벨 1-4
     */
    @field:Max(4)
    @field:Min(1)
    val level: Int,

    /**
     * 등급 구분
     */
    val type: MembershipType,

    /**
     * 구매 확정(실적금액)시 적립율
     */
    @field:DecimalMin(value = "0", inclusive = false)
    @field:DecimalMax(value = "1", inclusive = false)
    @field:Digits(integer = 1, fraction = 2)
    val creditRewardRate: Double,

    /**
     * 등급 부여 시점부터 현재까지 누적된 [MembershipEvent]::credit 총 합
     */
    @field:PositiveOrZero
    val totalCredit: Int,

    /**
     * 회원등급 상태
     */
    val status: MembershipStatus,

    /**
     * 등급 적용 시작일
     */
    val activeAt: Instant?,

    /**
     * 등급 만료일
     */
    val expireAt: Instant?,

    /**
     * 최초 생성일
     */
    @CreatedDate
    val createdAt: Instant? = null,

    /**
     * 최근 수정일
     */
    @LastModifiedDate
    val updatedAt: Instant? = null
) {
    companion object {
        /**
         * 회원 등급 생성
         *
         * @param customerId 고객 번호
         * @param policy 회원 등급 정책
         * @param status 회원 등급 상태
         * @param activeAt 활성화 시간
         */
        private fun of(
            customerId: String,
            policy: MembershipPolicy,
            status: MembershipStatus,
            activeAt: Instant?
        ): Membership = Membership(
            customerId = customerId,
            policyId = policy.id!!,
            level = policy.level,
            type = policy.type,
            creditRewardRate = policy.creditRewardRate,
            totalCredit = 0,
            status = status,
            activeAt = activeAt,
            expireAt = null
        )

        /**
         * 활성화 회원 등급 생성
         *
         * @param customerId 회원 번호
         * @param policy 회원 등급 정책
         */
        fun ofActive(customerId: String, policy: MembershipPolicy): Membership = of(
            customerId = customerId,
            policy = policy,
            status = MembershipStatus.ACTIVE,
            activeAt = Instant.now()
        )

        /**
         * 예상 회원 등급 생성
         *
         * @param customerId 회원 번호
         * @param policy 회원 등급 정책
         */
        fun ofExpected(customerId: String, policy: MembershipPolicy): Membership = of(
            customerId = customerId,
            policy = policy,
            status = MembershipStatus.EXPECTED,
            activeAt = null
        )
    }
}
