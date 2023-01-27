package io.commerce.membershipService.storeCredit

import io.commerce.membershipService.order.OrderService
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountAdminService
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@StoreCreditAccountServiceTest
@Import(
    StoreCreditAccountAdminService::class,
    OrderService::class
)
annotation class StoreCreditAccountAdminServiceTest
