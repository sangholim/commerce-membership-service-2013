package io.commerce.membershipService.storeCredit.transaction

import io.commerce.membershipService.fixture.faker
import io.commerce.membershipService.fixture.transaction
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing

@DataMongoTest
@TransactionServiceTest
@EnableReactiveMongoAuditing
class TransactionServiceGetAllByCustomerIdAndCriteriaTest(
    private val transactionRepository: TransactionRepository,
    private val transactionService: TransactionService
) : DescribeSpec({
    val customerId = faker.random.nextUUID()
    describe("suspend fun registerByDeposit(orderId: ObjectId?, customerId: String, amount: Int, note: String): Transaction") {
        context("거래 내역 구분이 '적립'인 경우") {
            val recordSize = 10
            val criteria = TransactionCriteria(TransactionType.DEPOSIT)

            beforeTest {
                transactionRepository
                    .saveAll(
                        List(recordSize) {
                            transaction {
                                this.customerId = customerId
                                this.type = TransactionType.DEPOSIT
                            }
                        }
                    )
                    .collect()
            }

            afterTest {
                transactionRepository.deleteAll()
            }

            it("적립금 '적립' 거래 내역 리스트가 조회된다") {
                transactionService.getAllByCustomerIdAndCriteria(customerId, criteria)
                    .toList()
                    .shouldHaveSize(recordSize)
                    .forAll {
                        it.type shouldBe TransactionType.DEPOSIT
                        it.createdAt shouldNotBe null
                    }
            }
        }

        context("거래 내역 구분이 '사용'인 경우") {
            val recordSize = 10
            val criteria = TransactionCriteria(TransactionType.CHARGE)

            beforeTest {
                transactionRepository
                    .saveAll(
                        List(recordSize) {
                            transaction {
                                this.customerId = customerId
                                this.type = TransactionType.CHARGE
                            }
                        }
                    )
                    .collect()
            }

            afterTest {
                transactionRepository.deleteAll()
            }

            it("적립금 '사용' 거래 내역 리스트가 조회된다") {
                transactionService.getAllByCustomerIdAndCriteria(customerId, criteria)
                    .toList()
                    .shouldHaveSize(recordSize)
                    .forAll {
                        it.type shouldBe TransactionType.CHARGE
                        it.createdAt shouldNotBe null
                    }
            }
        }
    }
})
