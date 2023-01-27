package io.commerce.membershipService.storeCredit.account

import org.mapstruct.Mapper

@Mapper
interface StoreCreditAccountMapper {
    fun toStoreCreditAccountView(storeCreditAccount: StoreCreditAccount): StoreCreditAccountView
}
