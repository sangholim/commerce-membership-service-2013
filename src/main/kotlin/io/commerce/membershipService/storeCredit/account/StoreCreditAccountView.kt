package io.commerce.membershipService.storeCredit.account

data class StoreCreditAccountView(
    /**
     * 고객 ID
     */
    val customerId: String,

    /**
     * 사용 가능한 총액
     */
    val balance: Int,

    /**
     * 다음달 소멸 예정 금액
     */
    val amountToExpire: Int
)
