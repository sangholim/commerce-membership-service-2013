package io.commerce.membershipService.storeCredit.transaction

import com.fasterxml.jackson.annotation.JsonView
import io.commerce.membershipService.core.Views
import io.commerce.membershipService.core.subject
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springdoc.api.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Tag(name = "적립금 거래 내역")
@SecurityRequirement(name = "aegis")
@RestController
@RequestMapping("/membership", produces = [MediaType.APPLICATION_JSON_VALUE])
class TransactionController(
    private val transactionService: TransactionService
) {
    /**
     * 적립금 거래 내역 조회
     * @param token 인증 토큰
     * @param criteria 질의 정보
     */
    @Operation(summary = "적립금 거래 내역 조회")
    @GetMapping("/store-credit/transactions")
    @JsonView(Views.Public::class)
    suspend fun getTransactions(
        token: BearerTokenAuthentication,
        @Valid @ParameterObject
        criteria: TransactionCriteria
    ): Flow<TransactionView> = transactionService.getAllByCustomerIdAndCriteria(token.subject, criteria)
}
