package io.commerce.membershipService.storeCredit

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Positive

/**
 * 적립금 지급 요청 데이터
 */
data class DepositPayload(
    /**
     * 적립금
     */
    @field: Positive(message = "금액이 0보다 커야 합니다")
    val amount: Int,

    /**
     * 지급 내역
     */
    @field: Length(min = 1, max = 20, message = "1자 이상 20자 이하 메세지를 작성해 주세요")
    val note: String
)
