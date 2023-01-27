package io.commerce.membershipService.storeCredit.transaction

import io.commerce.membershipService.storeCredit.ChargeStoreCreditPayload
import org.bson.types.ObjectId
import org.hibernate.validator.constraints.Length
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.Instant
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

/**
 * 적립금 거래 내역
 */
@Document
@CompoundIndex(def = "{ customerId: 1, type: 1 }")
data class Transaction(
    /**
     * 고유 ID
     */
    @MongoId
    val id: ObjectId? = null,

    /**
     * 적립금 지급에 사용된 주문 ID
     */
    val orderId: ObjectId?,

    /**
     * 고객 ID
     */
    @field: NotBlank
    val customerId: String,

    /**
     * 거래 구분
     */
    val type: TransactionType,

    /**
     * 거래 금액
     */
    @field: Positive
    val amount: Int,

    /**
     * 거래 요약
     */
    @field: Length(min = 1, max = 20)
    val note: String,

    /**
     * 최초 생성일
     */
    @Indexed(direction = IndexDirection.DESCENDING)
    @CreatedDate
    val createdAt: Instant? = null
) {
    companion object {
        private fun of(
            orderId: ObjectId? = null,
            customerId: String,
            type: TransactionType,
            amount: Int,
            note: String
        ) = Transaction(
            orderId = orderId,
            customerId = customerId,
            type = type,
            amount = amount,
            note = note
        )

        /**
         * 적립금 거래 내역 생성 (적립)
         * @param orderId 주문 ID
         * @param customerId 고객 ID
         * @param amount 적립 금액
         * @param note 적립 내역 상세
         */
        fun ofDeposit(
            orderId: ObjectId? = null,
            customerId: String,
            amount: Int,
            note: String
        ) = of(orderId = orderId, customerId = customerId, type = TransactionType.DEPOSIT, amount = amount, note = note)

        /**
         * 적립금 거래 내역 생성 (사용)
         *
         * @param customerId 고객 번호
         * @param payload 적립금 사용 payload
         * @param note 거래 요약
         */
        fun ofCharge(
            customerId: String,
            payload: ChargeStoreCreditPayload,
            note: String
        ) = of(
            orderId = payload.orderId,
            customerId = customerId,
            type = TransactionType.CHARGE,
            amount = payload.amount,
            note = note
        )
    }
}
