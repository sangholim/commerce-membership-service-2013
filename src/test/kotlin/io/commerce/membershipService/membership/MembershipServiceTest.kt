package io.commerce.membershipService.membership

import io.commerce.membershipService.membership.policy.MembershipPolicyServiceTest
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@MembershipPolicyServiceTest
@Import(
    MembershipService::class,
    MembershipMapperImpl::class,
    MembershipCrudService::class
)
annotation class MembershipServiceTest
