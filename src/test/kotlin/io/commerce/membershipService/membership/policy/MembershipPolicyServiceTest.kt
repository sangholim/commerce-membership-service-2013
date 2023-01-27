package io.commerce.membershipService.membership.policy

import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Import(
    MembershipPolicyService::class,
    MembershipPolicyMapperImpl::class
)
annotation class MembershipPolicyServiceTest
