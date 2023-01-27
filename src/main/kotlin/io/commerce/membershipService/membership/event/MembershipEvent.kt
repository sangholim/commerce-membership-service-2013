package io.commerce.membershipService.membership.event

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.Instant

/**
 * 회원등급 산정을 위한 이벤트 (히스토리) 관리
 */
@Document
data class MembershipEvent(
    /**
     * 이벤트 ID
     */
    @MongoId
    val id: ObjectId? = null,

    /**
     * 구매확정 기준 실적금액
     */
    val credit: Int,

    /**
     * 고객 ID
     */
    val customerId: String,

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

        fun of(customerId: String, credit: Int): MembershipEvent =
            MembershipEvent(credit = credit, customerId = customerId)
    }
}
