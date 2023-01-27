package io.commerce.membershipService.fixture

import io.commerce.membershipService.storeCredit.transaction.Transaction
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import org.bson.types.ObjectId
import java.time.Instant

inline fun transaction(block: TransactionFixtureBuilder.() -> Unit = {}) =
    TransactionFixtureBuilder().apply(block).build()

class TransactionFixtureBuilder {
    var id: ObjectId = ObjectId.get()
    var objectId: ObjectId = ObjectId.get()
    var customerId: String = faker.random.nextUUID()
    var type: TransactionType = TransactionType.DEPOSIT
    var amount: Int = faker.random.nextInt(100..200)
    var note: String = faker.random.randomString(20, false)
    var createdAt: Instant = Instant.now()

    fun build() = Transaction(
        id,
        objectId,
        customerId,
        type,
        amount,
        note,
        createdAt
    )
}
