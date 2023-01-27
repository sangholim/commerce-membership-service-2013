package io.commerce.membershipService.storeCredit.transaction

import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Import(
    TransactionService::class,
    TransactionMapperImpl::class
)
annotation class TransactionServiceTest
