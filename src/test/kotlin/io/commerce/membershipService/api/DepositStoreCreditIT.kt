package io.commerce.membershipService.api

import io.commerce.membershipService.core.ErrorResponse
import io.commerce.membershipService.core.SecurityConstants
import io.commerce.membershipService.fixture.depositPayload
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.fixture.storeCreditAccount
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountError
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestChannelBinderConfiguration::class)
class DepositStoreCreditIT(
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val transactionRepository: TransactionRepository,
    private val webTestClient: WebTestClient
) : BehaviorSpec({
    val tokenSubject = faker.random.nextUUID()
    val path = "/admin/membership/store-credit/account/$tokenSubject/deposit"

    fun getOpaqueToken(authority: String = SecurityConstants.SERVICE_ADMIN) =
        SecurityMockServerConfigurers.mockOpaqueToken()
            .authorities(SimpleGrantedAuthority(authority))
            .attributes { it["sub"] = tokenSubject }

    Given("관리자 적립금 지급 API 인증 실패") {
        When("인증이 없는 경우") {
            val request = webTestClient
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)

            Then("401 Unauthorized") {
                request
                    .exchange()
                    .expectStatus().isUnauthorized
            }
        }

        When("권한이 'service-admin' 아닌 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken(SecurityConstants.CUSTOMER))
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)

            Then("403 Forbidden") {
                request
                    .exchange()
                    .expectStatus().isForbidden
            }
        }
    }

    Given("관리자 적립금 지급 API 인증후") {
        When("필드 유효성 검사 실패") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    depositPayload {
                        this.amount = 0
                        this.note = ""
                    }
                )

            afterTest {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
            }

            Then("status: 400 Bad Request") {
                request.exchange()
                    .expectStatus().isBadRequest
            }

            Then("body: 다중 필드 에러") {
                request.exchange()
                    .expectBody(ErrorResponse::class.java)
                    .returnResult()
                    .responseBody!!.fields.count() shouldBe 2
            }
        }

        When("적립금 계좌가 없는 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    depositPayload {
                        this.amount = 1
                        this.note = "지급"
                    }
                )

            afterTest {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
            }

            Then("status: 400 Bad Request") {
                request.exchange()
                    .expectStatus().isBadRequest
            }

            Then("enum class: StoreCreditAccountError.ACCOUNT_NOT_FOUND") {
                request.exchange()
                    .expectBody(ErrorResponse::class.java)
                    .returnResult()
                    .responseBody!!.should {
                    it.code shouldBe StoreCreditAccountError.ACCOUNT_NOT_FOUND.code
                    it.message shouldBe StoreCreditAccountError.ACCOUNT_NOT_FOUND.message
                }
            }
        }

        When("적립금 지급 성공한 경우") {
            val amount = 1_000
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    depositPayload {
                        this.amount = amount
                        this.note = "지급"
                    }
                )

            beforeTest {
                storeCreditAccountRepository.save(
                    storeCreditAccount {
                        this.customerId = tokenSubject
                    }
                )
            }

            afterTest {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
            }

            Then("status: 204 No Content") {
                request.exchange()
                    .expectStatus().isNoContent
            }

            Then("적립금 계좌에 적립금 지급") {
                request.exchange()
                    .expectStatus().isNoContent
                    .expectBody().isEmpty

                storeCreditAccountRepository.findByCustomerId(tokenSubject)!!.should { storeCreditAccount ->
                    storeCreditAccount.balance shouldBe amount
                    storeCreditAccount.deposits.forExactly(1) { storeCredit ->
                        storeCredit.balance shouldBe amount
                    }
                }
            }

            Then("적립금 거래 내역 생성") {
                request.exchange()
                    .expectStatus().isNoContent
                    .expectBody().isEmpty

                transactionRepository.findAll().toList().forExactly(1) { transaction ->
                    transaction.amount shouldBe amount
                    transaction.customerId shouldBe tokenSubject
                    transaction.type shouldBe TransactionType.DEPOSIT
                }
            }
        }
    }
})
