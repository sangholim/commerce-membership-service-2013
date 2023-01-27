package io.commerce.membershipService.fixture

import io.commerce.membershipService.storeCredit.StoreCredit
import io.commerce.membershipService.storeCredit.account.StoreCreditAccount
import org.bson.types.ObjectId

inline fun storeCreditAccount(block: StoreCreditAccountAccountFixtureBuilder.() -> Unit = {}) =
    StoreCreditAccountAccountFixtureBuilder().apply(block).build()

class StoreCreditAccountAccountFixtureBuilder {
    var id: ObjectId = ObjectId.get()
    var customerId: String = faker.random.nextUUID()
    var amountToExpire: Int = 0
    var deposits: List<StoreCredit> = emptyList()

    fun build() = StoreCreditAccount(
        id = id,
        customerId = customerId,
        balance = deposits.sumOf(StoreCredit::balance),
        amountToExpire = amountToExpire,
        deposits = deposits
    )
}
