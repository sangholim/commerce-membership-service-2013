package io.commerce.membershipService.storeCredit.account

import io.commerce.membershipService.core.subject
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "적립금 계좌")
@SecurityRequirement(name = "aegis")
@RestController
@RequestMapping("/membership", produces = [APPLICATION_JSON_VALUE])
class StoreCreditAccountController(
    private val storeCreditAccountService: StoreCreditAccountService
) {
    @Operation(summary = "적립금 계좌 조회")
    @GetMapping("/store-credit/account")
    suspend fun getStoreCreditAccount(token: BearerTokenAuthentication): StoreCreditAccountView =
        storeCreditAccountService.getOrRegisterBy(token.subject)
}
