package io.commerce.membershipService.storeCredit.account

import io.commerce.membershipService.base.RestAdminController
import io.commerce.membershipService.storeCredit.DepositPayload
import io.commerce.membershipService.storeCredit.RefundPayload
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.Valid

@RestAdminController
class StoreCreditAccountAdminController(
    private val storeCreditAccountAdminService: StoreCreditAccountAdminService
) {
    /**
     * 관리자 권한으로 적립금을 지급합니다.
     * @param customerId 고객 ID
     * @param payload 적립금 지급 데이터
     */
    @Operation(summary = "관리자 적립금 지급")
    @PostMapping("/store-credit/account/{customerId}/deposit", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun depositStoreCredit(
        @PathVariable
        customerId: String,
        @Valid @RequestBody
        payload: DepositPayload
    ) {
        storeCreditAccountAdminService.deposit(customerId, payload)
    }

    /**
     * 관리자 권한으로 적립금을 반환합니다.
     * @param customerId 고객 ID
     * @param payload 적립금 반환 데이터
     */
    @Operation(summary = "관리자 적립금 반환")
    @PostMapping("/store-credit/account/{customerId}/refund", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun refundStoreCredit(
        @PathVariable
        customerId: String,
        @Valid @RequestBody
        payload: RefundPayload
    ) {
        storeCreditAccountAdminService.refund(customerId, payload)
    }
}
