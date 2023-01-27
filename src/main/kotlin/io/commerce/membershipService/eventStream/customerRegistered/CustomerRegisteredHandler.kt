package io.commerce.membershipService.eventStream.customerRegistered

import io.commerce.membershipService.membership.MembershipService
import io.commerce.membershipService.storeCredit.account.StoreCreditAccountService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message

/**
 * 고객 회원 가입 이벤트 처리 클래스
 *
 */
@Configuration
class CustomerRegisteredHandler(
    private val membershipService: MembershipService,
    private val storeCreditAccountService: StoreCreditAccountService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 고객 회원 가입 이벤트 처리 함수
     */
    @Bean
    fun customerRegisteredConsumer(): suspend (Flow<Message<CustomerRegisteredPayload>>) -> Unit = { flow ->
        flow
            .transform { message ->
                message
                    .runCatching { payload }
                    .onFailure { log.error("처리할 수 없는 message입니다", it) }
                    .onSuccess { emit(it) }
            }
            .collect { payload ->
                payload
                    .runCatching {
                        membershipService.registerBy(payload.customerId)
                        storeCreditAccountService.registerBy(payload.customerId)
                    }
                    .onFailure {
                        log.error("customer-registered 이벤트 처리에 실패하였습니다", it)
                    }
            }
    }
}
