package io.commerce.membershipService.storeCredit.account

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreCreditAccountRepository : CoroutineCrudRepository<StoreCreditAccount, String> {
    suspend fun findByCustomerId(customerId: String): StoreCreditAccount?

    suspend fun existsByCustomerId(customerId: String): Boolean
}
