package io.commerce.membershipService.order

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.MongoId
import javax.validation.constraints.NotBlank

data class Order(
    /**
     * 주문서 ID
     */
    @MongoId
    val id: ObjectId,

    /**
     * 주문 번호
     */
    @field: NotBlank
    val number: String,

    /**
     * 고객 ID
     */
    @field: NotBlank
    val customerId: String
)
