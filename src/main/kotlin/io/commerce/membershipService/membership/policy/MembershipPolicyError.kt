package io.commerce.membershipService.membership.policy

import io.commerce.membershipService.core.BaseError

enum class MembershipPolicyError(override val message: String) : BaseError {
    MEMBERSHIP_POLICY_NOT_FOUND("회원 등급 정책이 존재하지 않습니다")
}
