package io.commerce.membershipService.api

import io.commerce.membershipService.core.SecurityConstants
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.fixture.storeCreditAccount
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountView
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestChannelBinderConfiguration::class)
class GetStoreCreditAccountIT(
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val webTestClient: WebTestClient
) : BehaviorSpec({
    val path = "/membership/store-credit/account"

    val tokenSubject = faker.random.nextUUID()
    fun getOpaqueToken(authority: String = SecurityConstants.CUSTOMER) = SecurityMockServerConfigurers.mockOpaqueToken()
        .authorities(SimpleGrantedAuthority(authority))
        .attributes { it["sub"] = tokenSubject }

    beforeTest {
        storeCreditAccountRepository.deleteAll()
    }

    Given("적립금 계좌 조회 API 인증 실패") {
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

    Given("적립금 계좌 조회 API 인증후") {
        When("적립금 계좌가 없는 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .get().uri(path)

            Then("status: 200 Ok") {
                request
                    .exchange()
                    .expectStatus().isOk
            }

            Then("새로운 적립금 계좌가 생성된다") {
                request
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<StoreCreditAccountView>()
                    .returnResult().responseBody
                    .shouldNotBeNull()
                    .should {
                        it.amountToExpire shouldNotBe null
                        it.balance shouldNotBe null
                        it.customerId shouldNotBe null
                    }
            }
        }

        When("적립금 계좌 조회된 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .get().uri(path)

            beforeTest {
                storeCreditAccountRepository.save(
                    storeCreditAccount {
                        this.customerId = tokenSubject
                    }
                )
            }

            Then("200 OK with StoreCreditAccountView") {
                request
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<StoreCreditAccountView>()
                    .returnResult().responseBody
                    .shouldNotBeNull()
                    .should {
                        it.amountToExpire shouldNotBe null
                        it.balance shouldNotBe null
                        it.customerId shouldNotBe null
                    }
            }
        }
    }
})
