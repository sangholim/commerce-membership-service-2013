package io.commerce.membershipService.membership.policy

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "회원 등급 정책")
@SecurityRequirement(name = "aegis")
@RestController
@RequestMapping("/membership", produces = [MediaType.APPLICATION_JSON_VALUE])
class MembershipPolicyController(
    private val membershipPolicyService: MembershipPolicyService
) {
    /**
     * 회원 등급 정책 목록
     */
    @Operation(summary = "회원 등급 정책 목록")
    @GetMapping("/policies")
    suspend fun getMembershipPolicies(): List<MembershipPolicyView> =
        membershipPolicyService.getAllViews()
}
