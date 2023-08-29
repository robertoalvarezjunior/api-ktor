package com.cafeteria.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = System.getenv("audience")
    val jwtDomain = System.getenv("issuer")
    val jwtRealm = System.getenv("realm")
    val jwtSecret = System.getenv("secret")
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(
                    credential.payload
                ) else null
            }
            challenge { defaultScheme, realm ->
                call.respond(
                    HttpStatusCode
                        .Unauthorized, "Token is not valid or has expired"
                )
            }
        }
    }
}
