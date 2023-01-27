package io.commerce.membershipService.eventStream.orderRefund

import io.commerce.membershipService.storeCredit.StoreCreditRefundService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message

@Configuration
class OrderRefundHandler(
    private val storeCreditRefundService: StoreCreditRefundService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 주문 취소 이벤트 처리 함수
     */
    @Bean
    fun orderRefundConsumer(): suspend (Flow<Message<OrderRefundPayload>>) -> Unit = { flow ->
        flow
            .transform { message ->
                message
                    .runCatching { payload }
                    .onFailure { log.error("처리할 수 없는 message입니다", it) }
                    .onSuccess { emit(it) }
            }
            .collect { payload ->
                payload.runCatching {
                    storeCreditRefundService.refund(payload)
                }.onFailure {
                    log.error("order-refund 이벤트 처리에 실패하였습니다", it)
                }
            }
    }
}
