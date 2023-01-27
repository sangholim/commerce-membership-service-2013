package io.commerce.membershipService.storeCredit

import io.commerce.membershipService.storeCredit.account.AdjustStoreCreditAccountCallback
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountMapperImpl
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountService
import io.commerce.membershipService.storeCredit.transaction.TransactionServiceTest
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@TransactionServiceTest
@Import(
    StoreCreditAccountService::class,
    StoreCreditAccountMapperImpl::class,
    AdjustStoreCreditAccountCallback::class
)
annotation class StoreCreditAccountServiceTest
