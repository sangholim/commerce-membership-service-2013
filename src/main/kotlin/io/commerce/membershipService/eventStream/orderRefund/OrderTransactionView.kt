package io.commerce.membershipService.eventStream.orderRefund

import io.commerce.membershipService.storeCredit.transaction.TransactionType
import org.bson.types.ObjectId
import java.time.Instant
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

/**
 * 주문서의 적립금 거래 내역
 */
data class OrderTransactionView(
    /**
     * 고유 ID
     */
    val id: ObjectId,

    /**
     * 적립금 지급에 사용된 주문 ID
     */
    val orderId: ObjectId,

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
    @field: NotBlank
    val note: String,

    /**
     * 최초 생성일
     */
    val createdAt: Instant
)
