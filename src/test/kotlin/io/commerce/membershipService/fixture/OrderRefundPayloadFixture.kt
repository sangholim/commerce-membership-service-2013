package io.commerce.membershipService.fixture

import io.commerce.membershipService.eventStream.orderRefund.OrderRefundPayload
import io.commerce.membershipService.eventStream.orderRefund.OrderStoreCredit
import org.bson.types.ObjectId

inline fun orderRefundPayload(block: OrderRefundPayloadFixtureBuilder.() -> Unit = {}) =
    OrderRefundPayloadFixtureBuilder().apply(block).build()

class OrderRefundPayloadFixtureBuilder {
    var orderId: ObjectId = ObjectId.get()
    var storeCredit: OrderStoreCredit = orderStoreCredit()

    fun build() = OrderRefundPayload(
        orderId = orderId,
        storeCredit = storeCredit
    )
}
