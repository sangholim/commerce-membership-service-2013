package io.commerce.membershipService.order

import io.commerce.membershipService.core.BaseError

enum class OrderError(override val message: String) : BaseError {
    ORDER_NOT_FOUND("주문서를 찾을수 없습니다")
}
