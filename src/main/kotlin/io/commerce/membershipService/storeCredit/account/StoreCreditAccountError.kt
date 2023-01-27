package io.commerce.membershipService.storeCredit.account

import io.commerce.membershipService.core.BaseError

enum class StoreCreditAccountError(override val message: String) : BaseError {
    ACCOUNT_NOT_FOUND("적립금 계좌를 찾을 수 없습니다"),

    ACCOUNT_ALREADY_EXISTS("적립금 계좌를 보유한 고객입니다"),

    INSUFFICIENT_BALANCE("적립금이 부족합니다")
}
