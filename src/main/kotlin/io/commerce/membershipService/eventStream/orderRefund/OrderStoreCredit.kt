package io.commerce.membershipService.eventStream.orderRefund

import javax.validation.constraints.PositiveOrZero

/**
 * 주문서에서 사용한 적립금
 */
data class OrderStoreCredit(
    /**
     * 사용할 적립금액
     */
    @field: PositiveOrZero
    val amount: Int,

    /**
     * 적립금 차감 내역
     */
    val transaction: OrderTransactionView?
)
