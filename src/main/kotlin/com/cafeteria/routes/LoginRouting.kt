package com.cafeteria.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cafeteria.models.User
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import java.util.*


fun Route.loginRouting(database: MongoDatabase) {
    route("/login") {
        val collection = database.getCollection<User>("users")
        post {

            try {
                val user = call.receive<User>()

                val token = JWT.create()
                    .withAudience(System.getenv("audience"))
                    .withIssuer(System.getenv("issuer"))
//                .withClaim("email", user.email)
                    .withExpiresAt(Date(System.currentTimeMillis() + 600000))
                    .withPayload(mapOf("email" to user.email))
                    .sign(Algorithm.HMAC256(System.getenv("secret")))

                val validUser = collection.find(eq("email", user.email))

                if (validUser.toList().isNotEmpty()) {
                    if (validUser.first().senha == user.senha) {
                        call.respond(
                            hashMapOf("token" to token, "email" to user.email)
                        )
                    } else {
                        call.respondText(
                            "Senha incorreta",
                            status = HttpStatusCode.NotFound
                        )
                    }
                } else {
                    call.respondText(
                        "Usuario não encontrado",
                        status = HttpStatusCode.NotFound
                    )
                }

            } catch (e: Exception) {
                call.respondText("${e.message}")
            }
        }

    }
    route("/getUsers") {
        authenticate {
            get {
                val principal = call.principal<JWTPrincipal>()
                val audience = principal !!.payload.getClaim("email").asString()
                val expiresAt =
                    principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $audience! Token is expired at $expiresAt ms.")
            }
        }
    }
}

