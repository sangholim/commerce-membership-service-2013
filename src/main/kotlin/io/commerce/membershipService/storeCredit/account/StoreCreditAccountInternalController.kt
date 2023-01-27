package io.commerce.membershipService.storeCredit.account

import com.fasterxml.jackson.annotation.JsonView
import io.commerce.membershipService.core.Views
import io.commerce.membershipService.storeCredit.ChargeStoreCreditPayload
import io.commerce.membershipService.storeCredit.transaction.TransactionView
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Tag(name = "Internal")
@RestController
@RequestMapping("/internal/membership", produces = [MediaType.APPLICATION_JSON_VALUE])
class StoreCreditAccountInternalController(
    private val storeCreditAccountService: StoreCreditAccountService
) {
    @Operation(summary = "적립금 차감")
    @PostMapping("/store-credit/account/{customerId}/charge")
    @JsonView(Views.Internal::class)
    suspend fun chargeStoreCredit(
        @PathVariable customerId: String,
        @Valid @RequestBody
        payload: ChargeStoreCreditPayload
    ): TransactionView = storeCreditAccountService.charge(customerId, payload)
}
