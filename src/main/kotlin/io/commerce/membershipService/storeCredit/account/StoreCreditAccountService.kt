package io.commerce.membershipService.storeCredit.account

import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.storeCredit.ChargeStoreCreditPayload
import io.commerce.membershipService.storeCredit.StoreCredit
import io.commerce.membershipService.storeCredit.transaction.TransactionService
import io.commerce.membershipService.storeCredit.transaction.TransactionView
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreCreditAccountService(
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val transactionService: TransactionService,
    private val storeCreditAccountMapper: StoreCreditAccountMapper
) {
    /**
     * 적립금 계좌 조회
     * 없는 경우, 새로운 계좌 생성
     * @param customerId 고객 ID
     */
    suspend fun getOrRegisterBy(customerId: String): StoreCreditAccountView {
        val storeCreditAccount = storeCreditAccountRepository.findByCustomerId(customerId) ?: registerBy(customerId)
        return storeCreditAccountMapper.toStoreCreditAccountView(storeCreditAccount)
    }


    suspend fun getBy(customerId: String): StoreCreditAccount =
        storeCreditAccountRepository.findByCustomerId(customerId)
            ?: throw ErrorCodeException.of(StoreCreditAccountError.ACCOUNT_NOT_FOUND)

    @Transactional
    suspend fun registerBy(customerId: String): StoreCreditAccount {
        if (storeCreditAccountRepository.existsByCustomerId(customerId)) {
            throw ErrorCodeException.of(StoreCreditAccountError.ACCOUNT_ALREADY_EXISTS)
        }
        return storeCreditAccountRepository.save(StoreCreditAccount.of(customerId))
    }

    /**
     * 적립금 계좌에 적립금 지급
     *
     * @param customerId 고객 ID
     * @param storeCredit 적립금 데이터
     * @param note 적립금 거래 내역 상세
     */
    @Transactional
    suspend fun deposit(
        customerId: String,
        storeCredit: StoreCredit,
        note: String
    ): StoreCreditAccount = storeCreditAccountRepository
        .save(getBy(customerId).add(storeCredit))
        .also { transactionService.registerByDeposit(storeCredit.orderId, customerId, storeCredit.amount, note) }

    /**
     * 적립금 계좌로 부터 적립금 사용
     *
     * @param customerId 고객 번호
     * @param payload 적립금 사용 payload
     */
    @Transactional
    suspend fun charge(customerId: String, payload: ChargeStoreCreditPayload): TransactionView =
        getBy(customerId).let { account ->
            if (account.balance < payload.amount) {
                throw ErrorCodeException.of(StoreCreditAccountError.INSUFFICIENT_BALANCE)
            }

            storeCreditAccountRepository.save(getBy(customerId).charge(payload.amount))
            transactionService.registerByCharge(customerId, payload)
        }
}
