package io.commerce.membershipService.storeCredit.account

import kotlinx.coroutines.reactive.publish
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import org.springframework.stereotype.Component

@Component
class AdjustStoreCreditAccountCallback : ReactiveBeforeConvertCallback<StoreCreditAccount> {
    override fun onBeforeConvert(entity: StoreCreditAccount, collection: String): Publisher<StoreCreditAccount> =
        publish {
            send(entity.adjust())
        }
}
