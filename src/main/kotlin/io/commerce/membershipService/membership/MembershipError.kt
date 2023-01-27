package io.commerce.membershipService.membership

import io.commerce.membershipService.core.BaseError

enum class MembershipError(override val message: String) : BaseError {
    MEMBERSHIP_ALREADY_EXISTS("사용중인 회원 등급이 있습니다")
}
