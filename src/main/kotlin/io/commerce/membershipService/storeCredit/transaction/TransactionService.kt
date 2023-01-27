package io.commerce.membershipService.storeCredit.transaction

import io.commerce.membershipService.storeCredit.ChargeStoreCreditPayload
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class TransactionService(
    private val transactionMapper: TransactionMapper,
    private val transactionRepository: TransactionRepository
) {
    /**
     * 적립금 거래 내역 조회
     *
     * @param customerId 고객 ID
     * @param criteria 질의 정보
     */
    fun getAllByCustomerIdAndCriteria(customerId: String, criteria: TransactionCriteria): Flow<TransactionView> {
        val createdAt = ZonedDateTime.now().minusYears(1).toInstant()
        return transactionRepository.getViewByCustomerIdAndTypeAndCreatedAtAfter(
            customerId,
            criteria.type,
            createdAt,
            criteria.toPageRequest(Sort.by("createdAt").descending())
        )
    }

    /**
     * 적립금 거래 내역 생성 (적립)
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param amount 적립 금액
     * @param note 거래 내역 상세
     */
    suspend fun registerByDeposit(orderId: ObjectId?, customerId: String, amount: Int, note: String): Transaction =
        transactionRepository.save(
            Transaction.ofDeposit(
                orderId = orderId,
                customerId = customerId,
                amount = amount,
                note = note
            )
        )

    /**
     * 적립금 거래 내역 생성 (사용)
     *
     * @param customerId 고객 번호
     * @param payload 적립금 사용 payload
     */
    suspend fun registerByCharge(customerId: String, payload: ChargeStoreCreditPayload): TransactionView =
        transactionRepository.save(Transaction.ofCharge(customerId, payload, TransactionNotes.PURCHASE_CHARGE))
            .let(transactionMapper::toTransactionView)
}
