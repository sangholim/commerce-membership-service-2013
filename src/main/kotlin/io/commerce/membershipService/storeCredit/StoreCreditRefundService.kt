package io.commerce.membershipService.storeCredit

import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.eventStream.orderRefund.OrderRefundPayload
import io.commerce.membershipService.storeCredit.account.StoreCreditAccount
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountService
import io.commerce.membershipService.storeCredit.transaction.TransactionNotes
import org.springframework.stereotype.Service
import java.time.ZoneId

@Service
class StoreCreditRefundService(
    private val storeCreditAccountService: StoreCreditAccountService
) {
    /**
     * 주문서로부터 적립금 반환
     * order-db 의 데이터와 유효성 검사는 하지 않음
     *
     * @param payload 주문서 반환 이벤트 데이터
     */
    suspend fun refund(payload: OrderRefundPayload): StoreCreditAccount =
        payload.storeCredit.transaction?.let { transaction ->
            if (transaction.amount <= 0) throw ErrorCodeException.of(StoreCreditError.INVALID_TRANSACTION)

            val expiry = transaction.createdAt.atZone(ZoneId.systemDefault()).plusYears(1).toInstant()
            val storeCredit = StoreCredit.of(
                orderId = payload.orderId,
                amount = transaction.amount,
                issuedAt = transaction.createdAt,
                expiry = expiry
            )

            storeCreditAccountService.deposit(
                transaction.customerId,
                storeCredit,
                TransactionNotes.STORE_CREDIT_REFUND
            )
        } ?: throw ErrorCodeException.of(StoreCreditError.MISSING_TRANSACTION)
}
