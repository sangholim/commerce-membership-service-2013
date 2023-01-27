package io.commerce.membershipService.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(
    basePackages = [
        "io.commerce.membershipService.storeCredit",
        "io.commerce.membershipService.membership"
    ]
)
class MembershipMongoTemplateConfig
