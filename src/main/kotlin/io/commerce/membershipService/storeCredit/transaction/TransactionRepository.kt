package io.commerce.membershipService.storeCredit.transaction

import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.Instant

interface TransactionRepository : CoroutineCrudRepository<Transaction, ObjectId> {
    fun getViewByCustomerIdAndTypeAndCreatedAtAfter(
        customerId: String,
        type: TransactionType,
        createdAt: Instant,
        pageable: Pageable
    ): Flow<TransactionView>
}
