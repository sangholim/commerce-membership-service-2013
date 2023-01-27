package io.commerce.membershipService.api

import io.commerce.membershipService.fixture.chargeStoreCreditPayload
import io.commerce.membershipService.fixture.storeCredit
import io.commerce.membershipService.fixture.storeCreditAccount
import io.commerce.membershipService.storeCredit.StoreCredit
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.storeCredit.transaction.TransactionType
import io.commerce.membershipService.storeCredit.transaction.TransactionView
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.clearAllMocks
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.ZonedDateTime
import java.util.*

@SpringBootTest
@AutoConfigureWebTestClient
@Import(TestChannelBinderConfiguration::class)
class ChargeStoreCreditIT(
    private val storeCreditAccountRepository: StoreCreditAccountRepository,
    private val webTestClient: WebTestClient
) : BehaviorSpec({
    val customerId = UUID.randomUUID().toString()
    val path = "/internal/membership/store-credit/account/$customerId/charge"
    val request = webTestClient.post().uri(path)

    afterEach {
        clearAllMocks()
    }

    Given("적립금 지출") {
        When("payload 가 유효하지 않을 때") {
            val payload = chargeStoreCreditPayload {
                amount = -1
            }
            Then("Response 400 BAD_REQUEST") {
                request
                    .bodyValue(payload)
                    .exchange()
                    .expectStatus().isBadRequest
            }
        }
        When("customerId 가 존재하지 않을 때") {
            val payload = chargeStoreCreditPayload()
            Then("Response 400 BAD_REQUEST") {
                request
                    .bodyValue(payload)
                    .exchange()
                    .expectStatus().isBadRequest
            }
        }
        When("사용금액이 적립금보다 클 때") {
            val payload = chargeStoreCreditPayload {
                amount = 1_000_000
            }
            val account = storeCreditAccount {
                this.customerId = customerId
                deposits = listOf(
                    storeCredit {
                        val date = ZonedDateTime.parse("2021-12-17T06:46:52.091Z")
                        amount = 11_000
                        issuedAt = date.toInstant()
                        expiry = date.plusYears(1).toInstant()
                    },
                    storeCredit {
                        val date = ZonedDateTime.parse("2022-01-17T06:46:52.091Z")
                        amount = 10_000
                        issuedAt = date.toInstant()
                        expiry = date.plusYears(1).toInstant()
                    },
                    storeCredit {
                        val date = ZonedDateTime.parse("2022-02-17T06:46:52.091Z")
                        amount = 9_000
                        issuedAt = date.toInstant()
                        expiry = date.plusYears(1).toInstant()
                    },
                    storeCredit {
                        val date = ZonedDateTime.parse("2022-03-17T06:46:52.091Z")
                        amount = 8_000
                        issuedAt = date.toInstant()
                        expiry = date.plusYears(1).toInstant()
                    },
                    storeCredit {
                        val date = ZonedDateTime.parse("2022-04-17T06:46:52.091Z")
                        amount = 7_000
                        issuedAt = date.toInstant()
                        expiry = date.plusYears(1).toInstant()
                    }
                )
            }

            beforeEach {
                storeCreditAccountRepository.save(account)
            }

            afterEach {
                storeCreditAccountRepository.deleteAll()
            }

            Then("exception") {
                request
                    .bodyValue(payload)
                    .exchange()
                    .expectStatus().isBadRequest
            }
        }
        When("isOk") {
            val payload = chargeStoreCreditPayload {
                amount = 5_000
            }
            val account = storeCreditAccount {
                this.customerId = customerId
                deposits = listOf(
                    storeCredit {
                        val date = ZonedDateTime.parse("2021-12-17T06:46:52.091Z")
                        amount = 10_000
                        issuedAt = date.toInstant()
                        expiry = date.plusYears(1).toInstant()
                    },
                    storeCredit {
                        val date = ZonedDateTime.parse("2022-01-17T06:46:52.091Z")
                        amount = 10_000
                        issuedAt = date.toInstant()
                        expiry = date.plusYears(1).toInstant()
                    }
                )
            }

            beforeEach {
                storeCreditAccountRepository.save(account)
            }

            afterEach {
                storeCreditAccountRepository.deleteAll()
            }

            Then("status: 200 Ok") {
                request
                    .bodyValue(payload)
                    .exchange()
                    .expectStatus().isOk
            }

            Then("body: TransactionView") {
                request
                    .bodyValue(payload)
                    .exchange()
                    .expectBody<TransactionView>()
                    .returnResult()
                    .responseBody
                    .shouldNotBeNull()
                    .should {
                        it.orderId shouldBe payload.orderId
                        it.customerId shouldBe customerId
                        it.type shouldBe TransactionType.CHARGE
                        it.amount shouldBe payload.amount
                        it.note.shouldNotBeEmpty()
                        it.createdAt.shouldNotBeNull()
                    }
            }

            Then("적립금에서 사용된 금액이 정상 차감된다.") {
                val expectedBalance = account.deposits.sumOf(StoreCredit::balance) - payload.amount
                request
                    .bodyValue(payload)
                    .exchange()

                val result = storeCreditAccountRepository.findByCustomerId(customerId)
                result!!.deposits.sumOf(StoreCredit::balance) shouldBe expectedBalance
            }
        }
    }
})
