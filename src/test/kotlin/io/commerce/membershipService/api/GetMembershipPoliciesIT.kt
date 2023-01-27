package io.commerce.membershipService.api

import io.commerce.membershipService.core.SecurityConstants
import io.commerce.membershipService.fixture.MembershipPolicyFixture
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.membership.MembershipType
import io.commerce.membershipService.membership.policy.MembershipPolicyRepository
import io.commerce.membershipService.membership.policy.MembershipPolicyView
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.collect
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestChannelBinderConfiguration::class)
class GetMembershipPoliciesIT(
    private val membershipPolicyRepository: MembershipPolicyRepository,
    private val webTestClient: WebTestClient
) : BehaviorSpec({
    val path = "/membership/policies"

    val tokenSubject = faker.random.nextUUID()
    fun getOpaqueToken(authority: String = SecurityConstants.CUSTOMER) = SecurityMockServerConfigurers.mockOpaqueToken()
        .authorities(SimpleGrantedAuthority(authority))
        .attributes { it["sub"] = tokenSubject }

    Given("회원 등급 정책 조회 API 인증 실패") {
        When("인증이 없는 경우") {
            val request = webTestClient
                .get().uri(path)

            Then("401 Unauthorized") {
                request
                    .exchange()
                    .expectStatus().isUnauthorized
            }
        }

        When("권한이 'customer' 아닌 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken("unknown"))
                .get().uri(path)

            Then("403 Forbidden") {
                request
                    .exchange()
                    .expectStatus().isForbidden
            }
        }
    }

    Given("회원 등급 정책 조회 API 인증후") {
        val policies = MembershipPolicyFixture.membershipPolicies
        When("회원 등급 정책 조회 성공한 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .get()
                .uri(path)

            beforeTest {
                membershipPolicyRepository.saveAll(policies).collect()
            }

            afterTest {
                membershipPolicyRepository.deleteAll()
            }

            Then("status: 200 Ok") {
                request.exchange()
                    .expectStatus().isOk
            }

            Then("level: 1, name: '메이트', maximumCredit: 50_000 회원 등급이 1개 존재한다.") {
                request.exchange()
                    .expectBodyList(MembershipPolicyView::class.java)
                    .returnResult()
                    .responseBody!!.forOne { membershipPolicyView ->
                        membershipPolicyView.name shouldBe MembershipType.MATE.label
                        membershipPolicyView.level shouldBe 1
                        membershipPolicyView.maximumCredit shouldBe 50_000
                    }
            }

            Then("level: 2, name: '화이트', maximumCredit: 100_000 회원 등급이 1개 존재한다.") {
                request.exchange()
                    .expectBodyList(MembershipPolicyView::class.java)
                    .returnResult()
                    .responseBody!!.forOne { membershipPolicyView ->
                        membershipPolicyView.name shouldBe MembershipType.WHITE.label
                        membershipPolicyView.level shouldBe 2
                        membershipPolicyView.maximumCredit shouldBe 100_000
                    }
            }

            Then("level: 3, name: '코랄', maximumCredit: 200_000 회원 등급이 1개 존재한다.") {
                request.exchange()
                    .expectBodyList(MembershipPolicyView::class.java)
                    .returnResult()
                    .responseBody!!.forOne { membershipPolicyView ->
                        membershipPolicyView.name shouldBe MembershipType.CORAL.label
                        membershipPolicyView.level shouldBe 3
                        membershipPolicyView.maximumCredit shouldBe 200_000
                    }
            }

            Then("level: 4 , name: '레드', maximumCredit = null 회원 등급이 1개 존재한다.") {
                request.exchange()
                    .expectBodyList(MembershipPolicyView::class.java)
                    .returnResult()
                    .responseBody!!.forOne { membershipPolicyView ->
                        membershipPolicyView.name shouldBe MembershipType.RED.label
                        membershipPolicyView.level shouldBe 4
                        membershipPolicyView.maximumCredit shouldBe null
                    }
            }
        }
    }
})
