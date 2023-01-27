package io.commerce.membershipService.storeCredit.account

import io.commerce.membershipService.order.OrderService
import io.commerce.membershipService.storeCredit.DepositPayload
import io.commerce.membershipService.storeCredit.RefundPayload
import io.commerce.membershipService.storeCredit.StoreCredit
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneId

@Service
class StoreCreditAccountAdminService(
    private val storeCreditAccountService: StoreCreditAccountService,
    private val orderService: OrderService
) {
    /**
     * 적립금 계좌에 적립금 지급
     *
     * @param customerId 고객 번호
     * @param payload 적립금 지급 데이터
     */
    @Transactional
    suspend fun deposit(
        customerId: String,
        payload: DepositPayload
    ) {
        val now = Instant.now()
        val expiry = now.atZone(ZoneId.systemDefault()).plusYears(1).toInstant()
        val storeCredit = StoreCredit.of(orderId = null, amount = payload.amount, issuedAt = now, expiry = expiry)
        storeCreditAccountService.deposit(customerId, storeCredit, payload.note)
    }

    /**
     * 적립금 반환
     * @param customerId 고객 ID
     * @param payload 적립금 반환 데이터
     */
    @Transactional
    suspend fun refund(
        customerId: String,
        payload: RefundPayload
    ) {
        val orderId = orderService.getBy(payload.orderNumber, customerId).id
        val now = Instant.now()
        val expiry = now.atZone(ZoneId.systemDefault()).plusYears(1).toInstant()
        val storeCredit = StoreCredit.of(orderId = orderId, amount = payload.amount, issuedAt = now, expiry = expiry)
        storeCreditAccountService.deposit(customerId, storeCredit, payload.note)
    }
}
