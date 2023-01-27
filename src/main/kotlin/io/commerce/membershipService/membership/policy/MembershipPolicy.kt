package io.commerce.membershipService.membership.policy

import io.commerce.membershipService.membership.MembershipType
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.Instant
import javax.validation.constraints.*

/**
 * 회원등급 정책 관리
 */
@Document
data class MembershipPolicy(
    /**
     * 정책 아이디
     */
    @MongoId
    val id: ObjectId? = null,

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
     * 등급 부여에 필요한 최소 누적 실적금액
     */
    @field:PositiveOrZero
    val minimumCredit: Int,

    /**
     * 구매 확정(실적금액)시 적립율
     */
    @field:DecimalMin(value = "0", inclusive = false)
    @field:DecimalMax(value = "1", inclusive = false)
    @field:Digits(integer = 1, fraction = 2)
    val creditRewardRate: Double,

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
)
