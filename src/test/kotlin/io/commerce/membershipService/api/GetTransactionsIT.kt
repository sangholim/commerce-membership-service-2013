package io.commerce.membershipService.api

import io.commerce.membershipService.core.ErrorResponse
import io.commerce.membershipService.core.SecurityConstants
import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.fixture.transaction
import io.commerce.membershipService.storeCredit.transaction.TransactionCriteria
import io.commerce.membershipService.storeCredit.transaction.TransactionRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import io.commerce.membershipService.storeCredit.transaction.TransactionView
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.collect
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.util.*

@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestChannelBinderConfiguration::class)
class GetTransactionsIT(
    private val webTestClient: WebTestClient,
    private val transactionRepository: TransactionRepository
) : BehaviorSpec({
    val path = "/membership/store-credit/transactions"

    val tokenSubject = faker.random.nextUUID()
    fun getOpaqueToken(authority: String = SecurityConstants.CUSTOMER) = SecurityMockServerConfigurers.mockOpaqueToken()
        .authorities(SimpleGrantedAuthority(authority))
        .attributes { it["sub"] = tokenSubject }

    beforeTest {
        transactionRepository.deleteAll()
    }

    Given("적립금 거래 내역 API 인증 실패") {
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

    Given("적립금 거래 내역 API 인증후") {
        When("criteria 유효성 검사 실패") {
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .get()
                .uri { builder ->
                    val (type, page, size) = TransactionCriteria(type = TransactionType.DEPOSIT, -1, 30)

                    builder.path(path)
                    builder.queryParam("type", type.name.lowercase(Locale.getDefault()))
                    builder.queryParam("page", page)
                    builder.queryParam("size", size)
                    builder.build()
                }

            Then("ErrorResponse 에 다중 필드 에러가 존재한다") {
                request
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody<ErrorResponse>()
                    .returnResult().responseBody
                    .shouldNotBeNull()
                    .should { body ->
                        body.fields.count() shouldBe 2
                    }
            }
        }

        When("적립금 거래 구분이 '소멸'인 경우") {
            val recordSize = 20
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .get()
                .uri { builder ->
                    val (type, page, size) = TransactionCriteria(type = TransactionType.EXPIRE)

                    builder.path(path)
                    builder.queryParam("type", type.name.lowercase(Locale.getDefault()))
                    builder.queryParam("page", page)
                    builder.queryParam("size", size)
                    builder.build()
                }

            beforeTest {
                transactionRepository
                    .saveAll(
                        List(recordSize) {
                            transaction {
                                this.customerId = tokenSubject
                                this.type = TransactionType.EXPIRE
                            }
                        }
                    )
                    .collect()
            }

            Then("200 OK with List<TransactionView>") {
                request
                    .exchange()
                    .expectStatus().isOk
                    .expectBodyList<TransactionView>()
                    .hasSize(recordSize)
                    .returnResult().responseBody
                    .shouldNotBeEmpty()
                    .forAll {
                        it.type shouldBe TransactionType.EXPIRE
                    }
            }
        }

        When("적립금 거래 구분이 '적립'인 경우") {
            val recordSize = 20
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .get()
                .uri { builder ->
                    val (type, page, size) = TransactionCriteria(type = TransactionType.DEPOSIT)

                    builder.path(path)
                    builder.queryParam("type", type.name.lowercase(Locale.getDefault()))
                    builder.queryParam("page", page)
                    builder.queryParam("size", size)
                    builder.build()
                }

            beforeTest {
                transactionRepository
                    .saveAll(
                        List(recordSize) {
                            transaction {
                                this.customerId = tokenSubject
                                this.type = TransactionType.DEPOSIT
                            }
                        }
                    )
                    .collect()
            }

            Then("body: List<TransactionView> 형태의 데이터가 조회된다") {
                request
                    .exchange()
                    .expectStatus().isOk
                    .expectBodyList<TransactionView>()
                    .hasSize(recordSize)
                    .returnResult().responseBody
                    .shouldNotBeEmpty()
                    .forAll {
                        it.type shouldBe TransactionType.DEPOSIT
                    }
            }
        }

        When("적립금 거래 구분이 '사용'인 경우") {
            val recordSize = 20
            val criteria = TransactionCriteria(type = TransactionType.CHARGE)
            val request = webTestClient
                .mutateWith(getOpaqueToken())
                .get()
                .uri { builder ->
                    builder.path(path)
                    builder.queryParam("type", criteria.type.name.lowercase(Locale.getDefault()))
                    builder.queryParam("page", criteria.page)
                    builder.queryParam("size", criteria.size)
                    builder.build()
                }

            beforeTest {
                transactionRepository
                    .saveAll(
                        List(recordSize) {
                            transaction {
                                this.customerId = tokenSubject
                                this.type = TransactionType.CHARGE
                            }
                        }
                    )
                    .collect()
            }

            Then("body: List<TransactionView> 형태의 데이터가 조회된다") {
                request
                    .exchange()
                    .expectStatus().isOk
                    .expectBodyList<TransactionView>()
                    .hasSize(recordSize)
                    .returnResult().responseBody
                    .shouldNotBeEmpty()
                    .forAll {
                        it.type shouldBe TransactionType.CHARGE
                    }
            }
        }
    }
})
