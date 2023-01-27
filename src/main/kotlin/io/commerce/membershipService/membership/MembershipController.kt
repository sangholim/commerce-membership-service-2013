package io.commerce.membershipService.membership

import io.commerce.membershipService.core.subject
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "회원 등급")
@SecurityRequirement(name = "aegis")
@RestController
@RequestMapping("/membership", produces = [MediaType.APPLICATION_JSON_VALUE])
class MembershipController(
    private val membershipService: MembershipService
) {
    /**
     * 회원 등급 조회
     */
    @Operation(summary = "회원 등급 조회")
    @GetMapping
    suspend fun getMembership(
        token: BearerTokenAuthentication
    ): MembershipView =
        membershipService.getOrRegisterBy(token.subject)
}
