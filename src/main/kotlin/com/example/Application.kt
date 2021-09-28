package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.AuthConfigs
import com.example.routes.registerAuthenticationRoutes
import com.example.routes.registerCustomerRoutes
import com.example.service.CustomerServiceImpl
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import org.litote.kmongo.KMongo

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
        }
    }

    install(ContentNegotiation) {
        json()
    }

    val client = KMongo.createClient()
    val database = client.getDatabase("ktor-sample")

    // Routes Registers
    registerAuthenticationRoutes(configs = AuthConfigs(secret, issuer, audience, myRealm), database)
    registerCustomerRoutes(customerService = CustomerServiceImpl(database))
}
