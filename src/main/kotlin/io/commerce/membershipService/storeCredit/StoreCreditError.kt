package io.commerce.membershipService.storeCredit

import io.commerce.membershipService.core.BaseError

enum class StoreCreditError(override val message: String) : BaseError {
    INVALID_TRANSACTION("잘못된 적립금 차감 내역입니다"),

    MISSING_TRANSACTION("적립금 차감 내역이 없습니다");
}
