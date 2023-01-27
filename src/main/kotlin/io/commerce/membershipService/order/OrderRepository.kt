package io.commerce.membershipService.order

import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderRepository : CoroutineCrudRepository<Order, ObjectId> {
    suspend fun findByNumberAndCustomerId(number: String, customerId: String): Order?
}
