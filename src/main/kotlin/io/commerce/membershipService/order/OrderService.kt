package io.commerce.membershipService.order

import io.commerce.membershipService.core.ErrorCodeException
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository
) {
    /**
     * 주문 번호, 고객 ID 기준 주문서 조회
     * @param number 주문 번호
     * @param customerId 고객 ID
     */
    suspend fun getBy(number: String, customerId: String): Order =
        orderRepository.findByNumberAndCustomerId(number, customerId)
            ?: throw ErrorCodeException.of(OrderError.ORDER_NOT_FOUND)
}
