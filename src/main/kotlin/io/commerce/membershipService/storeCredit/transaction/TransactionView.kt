package io.commerce.membershipService.storeCredit.transaction

import com.fasterxml.jackson.annotation.JsonView
import io.commerce.membershipService.core.Views
import org.bson.types.ObjectId
import java.time.Instant

@JsonView(Views.Public::class)
data class TransactionView(
    /**
     * 고유 ID
     */
    @JsonView(Views.Internal::class)
    val id: ObjectId?,

    /**
     * 적립금 지급에 사용된 주문 ID
     */
    @JsonView(Views.Internal::class)
    val orderId: ObjectId?,

    /**
     * 고객 ID
     */
    @JsonView(Views.Internal::class)
    val customerId: String?,

    /**
     * 거래 구분
     */
    val type: TransactionType,

    /**
     * 거래 금액
     */
    val amount: Int,

    /**
     * 거래 요약
     */
    val note: String,

    /**
     * 최초 생성일
     */
    val createdAt: Instant
)
