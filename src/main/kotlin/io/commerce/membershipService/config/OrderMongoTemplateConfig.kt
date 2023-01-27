package io.commerce.membershipService.config

import com.mongodb.reactivestreams.client.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(
    basePackages = ["io.commerce.membershipService.order"],
    reactiveMongoTemplateRef = "orderMongoTemplate"
)
class OrderMongoTemplateConfig(
    private val reactiveMongoClient: MongoClient
) {
    private val database = "order-db"

    @Bean
    fun orderMongoTemplate(): ReactiveMongoTemplate = ReactiveMongoTemplate(
        SimpleReactiveMongoDatabaseFactory(reactiveMongoClient, database)
    )
}
