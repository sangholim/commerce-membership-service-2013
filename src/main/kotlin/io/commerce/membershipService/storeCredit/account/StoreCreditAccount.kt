package io.commerce.membershipService.storeCredit.account

import io.commerce.membershipService.storeCredit.StoreCredit
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.Instant

/**
 * 적립금 계좌
 */
@Document
data class StoreCreditAccount(
    /**
     * 고유 ID
     */
    @MongoId
    val id: ObjectId? = null,

    /**
     * 고객 ID
     */
    @Indexed(unique = true)
    val customerId: String,

    /**
     * 사용 가능한 총액
     */
    val balance: Int,

    /**
     * 다음달 소멸 예정 금액
     */
    val amountToExpire: Int,

    /**
     * 지급된 모든 적립금
     */
    val deposits: List<StoreCredit>,

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
         * 고객의 적립금 계좌 생성
         *
         * @param customerId 고객 ID
         */
        fun of(customerId: String) = StoreCreditAccount(
            customerId = customerId,
            balance = 0,
            amountToExpire = 0,
            deposits = emptyList()
        )
    }
}
