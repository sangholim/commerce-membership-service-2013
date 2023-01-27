package io.commerce.membershipService.core

import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication

val BearerTokenAuthentication.subject: String
    get() = tokenAttributes["sub"] as String
