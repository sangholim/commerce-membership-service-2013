package io.commerce.membershipService.storeCredit

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import java.time.Instant
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero

/**
 * 적립금 지급 내역
 */
data class StoreCredit(
    /**
     * 적립금 지급에 사용된 주문 ID
     */
    val orderId: ObjectId?,

    /**
     * 지급된 총액
     */
    @field: Positive
    val amount: Int,

    /**
     * 사용 가능한 잔액
     */
    @field: PositiveOrZero
    val balance: Int,

    /**
     * 적립금의 만료 예정일
     */
    val expiry: Instant,

    /**
     * 적립금 지급일
     */
    @Indexed
    val issuedAt: Instant
) {
    companion object {
        fun of(
            orderId: ObjectId? = null,
            amount: Int,
            expiry: Instant,
            issuedAt: Instant
        ) = StoreCredit(
            orderId = orderId,
            amount = amount,
            balance = amount,
            expiry = expiry,
            issuedAt = issuedAt
        )
    }
}
