package io.commerce.membershipService.fixture

import io.commerce.membershipService.storeCredit.ChargeStoreCreditPayload
import org.bson.types.ObjectId

inline fun chargeStoreCreditPayload(block: ChargeStoreCreditPayloadFixture.() -> Unit = {}) =
    ChargeStoreCreditPayloadFixture().apply(block).build()

class ChargeStoreCreditPayloadFixture {
    var orderId: ObjectId = ObjectId.get()
    var amount: Int = 5000

    fun build() = ChargeStoreCreditPayload(
        orderId = orderId,
        amount = amount
    )
}
