package io.commerce.membershipService.fixture

import io.commerce.membershipService.eventStream.orderRefund.OrderTransactionView
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import org.bson.types.ObjectId
import java.time.Instant

inline fun orderTransactionView(block: OrderTransactionViewFixtureBuilder.() -> Unit = {}) =
    OrderTransactionViewFixtureBuilder().apply(block).build()

class OrderTransactionViewFixtureBuilder {
    var id: ObjectId = ObjectId.get()
    var orderId: ObjectId = ObjectId.get()
    var customerId: String = "customerId"
    var type: TransactionType = TransactionType.CHARGE
    var amount: Int = 1
    var note: String = faker.random.randomString(20, false)
    var createdAt: Instant = Instant.now()

    fun build() = OrderTransactionView(
        id,
        orderId,
        customerId,
        type,
        amount,
        note,
        createdAt
    )
}
