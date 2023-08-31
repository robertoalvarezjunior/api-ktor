package com.cafeteria.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cafeteria.models.Usuario
import com.cafeteria.models.UsuarioEndereco
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


fun Route.usuarioRouting(database: MongoDatabase) {
    route("/usuario") {
        val collection = database.getCollection<Usuario>("usuarios")
        get("/login") {
            try {
                val usuario = call.receive<Map<String, String>>()

                val validarUsuario = collection.find(eq("email", usuario["email"]))

                val token = JWT.create()
                    .withAudience(System.getenv("audience"))
                    .withIssuer(System.getenv("issuer"))
                    .withExpiresAt(Date(System.currentTimeMillis() + 600000))
                    .withPayload(mapOf("email" to usuario["email"]))
                    .sign(Algorithm.HMAC256(System.getenv("secret")))


                if (validarUsuario.toList().isNotEmpty()) {
                    if (validarUsuario.first().senha == usuario["senha"]) {
                        call.respond(
                            hashMapOf(
                                "token" to token,
                                "email" to validarUsuario.first
                                    ().email,
                                "nome" to validarUsuario.first().nome,
                                "numeroTelefone" to validarUsuario.first().numeroTelefone,
                                "endereco" to validarUsuario.first().endereco,
                            ),
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
        post("/cadastro") {
            try {
                val usuario = call.receive<Usuario>()
                val verificarUsuario =
                    collection.find(eq("email", usuario.email))

                if (verificarUsuario.toList().isNotEmpty()) {
                    call.respondText(
                        "Email já cadastrado",
                        status = HttpStatusCode.Conflict
                    )
                } else {
                    collection.insertOne(usuario)
                    call.respondText(
                        "Usuário cadastrado com sucesso",
                        status = HttpStatusCode.Created
                    )
                }

            } catch (e: Exception) {
                call.respondText("${e.message}")
            }
        }
        patch("/atualizarEndereco") {
            try {
                val usuarioEndereco = call.receive<UsuarioEndereco>()
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

