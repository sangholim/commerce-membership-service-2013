package io.commerce.membershipService.fixture

import io.commerce.membershipService.storeCredit.StoreCredit
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

inline fun storeCredit(block: StoreCreditFixtureBuilder.() -> Unit = {}) =
    StoreCreditFixtureBuilder().apply(block).build()

class StoreCreditFixtureBuilder {
    var orderId: ObjectId? = null
    var amount: Int = faker.random.nextInt(100..1_000)
    var balance: Int? = null
    var expiry: Instant = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusYears(1).toInstant()
    var issuedAt: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

    fun build() = StoreCredit(
        orderId = orderId,
        amount = amount,
        balance = balance ?: amount,
        expiry = expiry,
        issuedAt = issuedAt
    )
}
