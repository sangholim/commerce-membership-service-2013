package io.commerce.membershipService.fixture

import io.commerce.membershipService.storeCredit.RefundPayload

inline fun refundPayload(block: RefundPayloadFixtureBuilder.() -> Unit = {}) =
    RefundPayloadFixtureBuilder().apply(block).build()

class RefundPayloadFixtureBuilder {
    var orderNumber: String = faker.random.randomString(20)
    var amount: Int = 5_000
    var note: String = "관리자 적립금 반환"

    fun build() = RefundPayload(
        orderNumber,
        amount,
        note
    )
}
