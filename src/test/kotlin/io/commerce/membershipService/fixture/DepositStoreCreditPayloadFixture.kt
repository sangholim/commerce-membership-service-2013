package io.commerce.membershipService.fixture

import io.commerce.membershipService.storeCredit.DepositPayload

inline fun depositPayload(block: DepositPayloadFixtureBuilder.() -> Unit = {}) =
    DepositPayloadFixtureBuilder().apply(block).build()

class DepositPayloadFixtureBuilder {
    var amount: Int = 1
    var note: String = "적립금 지급요"

    fun build() = DepositPayload(
        amount,
        note
    )
}
