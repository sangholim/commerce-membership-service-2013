package io.commerce.membershipService.eventStream.orderRefund

import org.bson.types.ObjectId

/**
 * 주문서 취소 이벤트 데이터
 */
data class OrderRefundPayload(
    /**
     * 주문서 번호
     */
    val orderId: ObjectId,

    /**
     * 주문서에서 사용한 적립금
     */
    val storeCredit: OrderStoreCredit
)
