package io.commerce.membershipService.api

import io.commerce.membershipService.core.ErrorResponse
import io.commerce.membershipService.core.SecurityConstants
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.fixture.order
import io.commerce.membershipService.fixture.refundPayload
import io.commerce.membershipService.fixture.storeCreditAccount
import io.commerce.membershipService.order.OrderError
import io.commerce.membershipService.order.OrderRepository
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountError
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
class RefundStoreCreditIT(
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val transactionRepository: TransactionRepository,
    private val orderRepository: OrderRepository,
    private val webTestClient: WebTestClient
) : BehaviorSpec({
    val orderNumber = "HF-11231312321"
    val tokenSubject = faker.random.nextUUID()
    val path = "/admin/membership/store-credit/account/$tokenSubject/refund"

    fun getOpaqueToken(authority: String = SecurityConstants.SERVICE_ADMIN) =
        SecurityMockServerConfigurers.mockOpaqueToken()
            .authorities(SimpleGrantedAuthority(authority))
            .attributes { it["sub"] = tokenSubject }

    Given("관리자 적립금 반환 API 인증 실패") {
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

    Given("관리자 적립금 반환 API 인증후") {
        When("필드 유효성 검사 실패") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    refundPayload {
                        this.orderNumber = ""
                        this.amount = 0
                        this.note = ""
                    }
                )

            afterTest {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
                orderRepository.deleteAll()
            }

            Then("status: 400 Bad Request") {
                request.exchange()
                    .expectStatus().isBadRequest
            }

            Then("body: 다중 필드 에러") {
                request.exchange()
                    .expectBody(ErrorResponse::class.java)
                    .returnResult()
                    .responseBody!!.fields.count() shouldBe 3
            }
        }

        When("주문서가 없는 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    refundPayload {
                        this.orderNumber = "123123123123"
                        this.amount = 5_000
                        this.note = "관리자 적립금 환불"
                    }
                )

            beforeTest {
                orderRepository.save(
                    order {
                        this.number = orderNumber
                        this.customerId = tokenSubject
                    }
                )
            }

            afterTest {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
                orderRepository.deleteAll()
            }

            Then("status: 400 Bad Request") {
                request.exchange()
                    .expectStatus().isBadRequest
            }

            Then("enum class: OrderError.ORDER_NOT_FOUND") {
                request.exchange()
                    .expectBody(ErrorResponse::class.java)
                    .returnResult()
                    .responseBody!!.should {
                    it.code shouldBe OrderError.ORDER_NOT_FOUND.code
                    it.message shouldBe OrderError.ORDER_NOT_FOUND.message
                }
            }
        }

        When("적립금 계좌가 없는 경우") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    refundPayload {
                        this.orderNumber = orderNumber
                        this.amount = 5_000
                        this.note = "관리자 적립금 환불"
                    }
                )

            beforeTest {
                orderRepository.save(
                    order {
                        this.number = orderNumber
                        this.customerId = tokenSubject
                    }
                )
            }

            afterTest {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
                orderRepository.deleteAll()
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

        When("적립금 반환 성공한 경우") {
            val amount = 5_000
            val note = "관리자 적립금 반환"
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    refundPayload {
                        this.orderNumber = orderNumber
                        this.amount = amount
                        this.note = note
                    }
                )

            beforeTest {
                orderRepository.save(
                    order {
                        this.number = orderNumber
                        this.customerId = tokenSubject
                    }
                )
                storeCreditAccountRepository.save(
                    storeCreditAccount {
                        this.customerId = tokenSubject
                    }
                )
            }

            afterTest {
                storeCreditAccountRepository.deleteAll()
                transactionRepository.deleteAll()
                orderRepository.deleteAll()
            }

            Then("status: 204 No Content") {
                request.exchange()
                    .expectStatus().isNoContent
                    .expectBody().isEmpty
            }

            Then("적립금 계좌 balance 5_000 이다") {
                request.exchange()
                    .expectStatus().isNoContent
                    .expectBody().isEmpty

                storeCreditAccountRepository.findByCustomerId(tokenSubject)!!.should { storeCreditAccount ->
                    storeCreditAccount.customerId shouldBe tokenSubject
                    storeCreditAccount.balance shouldBe amount
                }
            }

            Then("적립금 계좌 내에 적립금 데이터 주문 ID가 존재한다") {
                request.exchange()
                    .expectStatus().isNoContent
                    .expectBody().isEmpty

                storeCreditAccountRepository.findByCustomerId(tokenSubject)!!.deposits
                    .forExactly(1) { storeCredit ->
                        storeCredit.orderId shouldNotBe null
                    }
            }

            Then("적립금 계좌 내에 적립금 데이터에는 amount, balance 가 5000 이다") {
                request.exchange()
                    .expectStatus().isNoContent
                    .expectBody().isEmpty

                storeCreditAccountRepository.findByCustomerId(tokenSubject)!!.deposits
                    .forExactly(1) { storeCredit ->
                        storeCredit.amount shouldBe amount
                        storeCredit.balance shouldBe amount
                    }
            }

            Then("적립금 거래 내역이 생성된다") {
                request.exchange()
                    .expectStatus().isNoContent
                    .expectBody().isEmpty

                transactionRepository.findAll().toList().forExactly(1) { transaction ->
                    transaction.customerId shouldBe tokenSubject
                    transaction.amount shouldBe amount
                    transaction.type shouldBe TransactionType.DEPOSIT
                }
            }
        }
    }
})
