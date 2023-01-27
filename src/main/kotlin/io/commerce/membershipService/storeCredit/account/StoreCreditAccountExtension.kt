package io.commerce.membershipService.storeCredit.account

import io.commerce.membershipService.storeCredit.StoreCredit
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import java.time.ZonedDateTime

/**
 * 적립금 계좌에 새로운 적립금 추가
 *
 * @param storeCredit 새로운 적립금
 */
fun StoreCreditAccount.add(storeCredit: StoreCredit) =
    copy(deposits = listOf(storeCredit) + deposits)

suspend fun StoreCreditAccount.charge(amount: Int): StoreCreditAccount {
    var remainder = amount
    return copy(
        deposits = deposits.asFlow().transform {
            if (remainder >= it.balance) {
                remainder -= it.balance
            } else {
                emit(it.copy(balance = it.balance - remainder))
                remainder = 0
            }
        }.toList()
    )
}

suspend fun StoreCreditAccount.adjust(): StoreCreditAccount {
    val timeLimit = ZonedDateTime.now().withDayOfMonth(1).plusMonths(2).toInstant()
    return copy(
        balance = deposits.sumOf(StoreCredit::balance),
        amountToExpire = deposits.asFlow().fold(0) { total, credit ->
            if (credit.expiry.isAfter(timeLimit)) {
                total
            } else {
                total + credit.balance
            }
        }
    )
}
