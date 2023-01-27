package io.commerce.membershipService.fixture

import io.commerce.membershipService.order.Order
import org.bson.types.ObjectId

inline fun order(block: OrderFixtureBuilder.() -> Unit = {}) =
    OrderFixtureBuilder().apply(block).build()

class OrderFixtureBuilder {
    var id: ObjectId = ObjectId.get()
    var number: String = faker.random.randomString(10)
    var customerId: String = faker.random.nextUUID()

    fun build() = Order(
        id,
        number,
        customerId
    )
}
