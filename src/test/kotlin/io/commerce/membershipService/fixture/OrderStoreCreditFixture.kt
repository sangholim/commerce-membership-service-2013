package io.commerce.membershipService.fixture

import io.commerce.membershipService.eventStream.orderRefund.OrderStoreCredit
import io.commerce.membershipService.eventStream.orderRefund.OrderTransactionView

inline fun orderStoreCredit(block: OrderStoreCreditFixtureBuilder.() -> Unit = {}) =
    OrderStoreCreditFixtureBuilder().apply(block).build()

class OrderStoreCreditFixtureBuilder {
    var amount: Int = 1
    var transaction: OrderTransactionView? = orderTransactionView()

    fun build() = OrderStoreCredit(
        amount,
        transaction
    )
}
