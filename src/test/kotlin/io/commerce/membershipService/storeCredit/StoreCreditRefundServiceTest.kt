package io.commerce.membershipService.storeCredit

import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@StoreCreditAccountServiceTest
@Import(StoreCreditRefundService::class)
annotation class StoreCreditRefundServiceTest
