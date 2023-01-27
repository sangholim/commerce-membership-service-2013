package io.commerce.membershipService.eventStream.customerRegistered

import javax.validation.constraints.NotBlank

/**
 * 고객 회원 가입 이벤트 payload
 *
 * @property customerId 고객 번호
 */
data class CustomerRegisteredPayload(
    @field: NotBlank
    val customerId: String
)
