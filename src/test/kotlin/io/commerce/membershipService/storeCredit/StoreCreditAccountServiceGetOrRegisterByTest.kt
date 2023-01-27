package io.commerce.membershipService.storeCredit

import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.fixture.storeCredit
import io.commerce.membershipService.fixture.storeCreditAccount
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountRepository
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing

@DataMongoTest
@EnableReactiveMongoAuditing
@StoreCreditAccountServiceTest
class StoreCreditAccountServiceGetOrRegisterByTest(
    private val storeCreditAccountService: StoreCreditAccountService,
    private val storeCreditAccountRepository: StoreCreditAccountRepository
) : BehaviorSpec({
    val customerId = faker.random.nextUUID()

    Given("적립금 계좌 조회하기") {
        When("적립금 계좌가 없는 경우") {

            afterEach {
                storeCreditAccountRepository.deleteAll()
            }

            Then("새로운 계정이 생성된다") {
                storeCreditAccountService.getOrRegisterBy(customerId)
                val storeCreditAccount = storeCreditAccountRepository.findByCustomerId(customerId).shouldNotBeNull()
                storeCreditAccount.id shouldNotBe null
            }
        }

        When("적립금 계좌가 존재하는 경우") {

            val amount = 1_000
            beforeEach {
                val storeCreditAccount = storeCreditAccount {
                    this.customerId = customerId
                    this.deposits = listOf(
                        storeCredit {
                            this.amount = amount
                            this.balance = amount
                        })
                }

                storeCreditAccountRepository.save(storeCreditAccount)
            }

            afterEach {
                storeCreditAccountRepository.deleteAll()
            }

            Then("기존 적립금 계좌를 조회한다") {
                val storeCreditAccount = storeCreditAccountService.getOrRegisterBy(customerId)
                storeCreditAccount.balance shouldBe amount
            }
        }
    }
})
