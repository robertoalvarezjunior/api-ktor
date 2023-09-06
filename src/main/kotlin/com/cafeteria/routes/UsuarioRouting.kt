package com.cafeteria.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cafeteria.models.Usuario
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

fun Route.usuarioRouting(database: MongoDatabase) {
    route("/usuario") {
        val collection = database.getCollection<Usuario>("usuarios")

        get {
            try {
                val body = call.receive<Map<String, String>>()

                val validarUsuario = collection.find(
                    eq(
                        Usuario::email.name,
                        body["email"]
                    )
                ).toList()

                val token = JWT.create()
                    .withAudience(System.getenv("audience"))
                    .withIssuer(System.getenv("issuer"))
                    .withExpiresAt(Date(System.currentTimeMillis() + 600000))
                    .withPayload(mapOf(Usuario::email.name to body["email"]))
                    .sign(Algorithm.HMAC256(System.getenv("secret")))


                if (validarUsuario.isNotEmpty()) {
                    if (validarUsuario.first().senha == body["senha"]
                    ) {

                        val encodeUser = Json.encodeToString(
                            Usuario(
                                idUsuario = validarUsuario.first().idUsuario,
                                email = validarUsuario.first().email,
                                senha = validarUsuario.first().senha,
                                nome = validarUsuario.first().nome,
                                numeroTelefone = validarUsuario.first().numeroTelefone,
                                enderecos = validarUsuario.first().enderecos,
                                carrinho = validarUsuario.first().carrinho
                            )
                        )

                        call.respond(
                            hashMapOf(
                                "token" to token,
                                "usuario" to encodeUser
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

        post {
            try {
                val body = call.receive<Usuario>()
                val verificarUsuario =
                    collection.find(eq("email", body.email))

                if (verificarUsuario.toList().isNotEmpty()) {
                    call.respondText(
                        "Email já cadastrado",
                        status = HttpStatusCode.Conflict
                    )
                } else {
                    val encodeUsuario = Json.encodeToString(
                        Usuario(
                            idUsuario = body.idUsuario ?: UUID.randomUUID().toString(),
                            email = body.email,
                            senha = body.senha,
                            nome = body.nome,
                            numeroTelefone = body.numeroTelefone,
                            enderecos = body.enderecos,
                            carrinho = body.carrinho
                        )
                    )

                    collection.insertOne(Json.decodeFromString(encodeUsuario))
                    call.respondText(
                        "Usuário cadastrado com sucesso",
                        status = HttpStatusCode.Created
                    )
                }

            } catch (e: Exception) {
                call.respondText("${e.message}")
            }
        }

    }
}

