package io.commerce.membershipService.storeCredit.transaction

import org.mapstruct.Mapper

@Mapper
interface TransactionMapper {

    fun toTransactionView(transaction: Transaction): TransactionView
}
