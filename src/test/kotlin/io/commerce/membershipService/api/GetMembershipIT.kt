package io.commerce.membershipService.api

import io.commerce.membershipService.membership.*
import io.commerce.membershipService.core.SecurityConstants
import io.commerce.membershipService.fixture.MembershipPolicyFixture
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.membership.policy.MembershipPolicyRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
class GetMembershipIT(
    private val membershipPolicyRepository: MembershipPolicyRepository,
    private val membershipRepository: MembershipRepository,
    private val membershipService: MembershipService,
    private val webTestClient: WebTestClient
) : BehaviorSpec({
    val tokenSubject = faker.random.nextUUID()
    val path = "/membership"

    fun getOpaqueToken(authority: String = SecurityConstants.CUSTOMER) =
        SecurityMockServerConfigurers.mockOpaqueToken()
            .authorities(SimpleGrantedAuthority(authority))
            .attributes { it["sub"] = tokenSubject }

    Given("회원 등급 조회 API 인증 실패") {
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

    Given("회원 등급 조회 API 인증후") {
        val policies = MembershipPolicyFixture.membershipPolicies
        When("회원 등급이 없는 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .get()
                .uri(path)

            beforeTest {
                membershipPolicyRepository.saveAll(policies).collect()
            }

            afterTest {
                membershipRepository.deleteAll()
                membershipPolicyRepository.deleteAll()
            }

            Then("status: 200 Ok") {
                request.exchange()
                    .expectStatus().isOk
            }

            Then("새로운 회원 등급 생성") {
                request.exchange()
                    .expectBody(MembershipView::class.java)
                    .returnResult()
                    .responseBody
                    .shouldNotBeNull()
                    .should { membership ->
                        membership.name shouldNotBe null
                        membership.creditRewardRate shouldNotBe null
                    }
            }
        }

        When("회원 등급이 존재하는 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .get()
                .uri(path)

            lateinit var membership: MembershipView

            beforeTest {
                membershipPolicyRepository.saveAll(policies).collect()
                membership = membershipService.registerBy(tokenSubject).first { it.status == MembershipStatus.ACTIVE }.toMembershipView()
            }

            afterTest {
                membershipRepository.deleteAll()
                membershipPolicyRepository.deleteAll()
            }

            Then("status: 200 Ok") {
                request.exchange()
                    .expectStatus().isOk
            }

            Then("기존 회원 등급 조회") {
                request.exchange()
                    .expectBody(MembershipView::class.java)
                    .returnResult()
                    .responseBody
                    .shouldNotBeNull()
                    .should {
                        it.name shouldBe membership.name
                        it.creditRewardRate shouldBe membership.creditRewardRate
                    }
            }
        }
    }
})
