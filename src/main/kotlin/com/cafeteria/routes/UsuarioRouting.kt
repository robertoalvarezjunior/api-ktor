package com.cafeteria.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cafeteria.models.Usuario
import com.cafeteria.models.UsuarioEndereco
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
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

        get("/login") {
            try {
                val usuario = call.receive<Map<String, String>>()

                val validarUsuario = collection.find(
                    eq(
                        Usuario::email.name,
                        usuario["email"]
                    )
                ).toList()

                val token = JWT.create()
                    .withAudience(System.getenv("audience"))
                    .withIssuer(System.getenv("issuer"))
                    .withExpiresAt(Date(System.currentTimeMillis() + 600000))
                    .withPayload(mapOf(Usuario::email.name to usuario["email"]))
                    .sign(Algorithm.HMAC256(System.getenv("secret")))


                if (validarUsuario.isNotEmpty()) {
                    if (validarUsuario.first().senha == usuario["senha"]) {

                        val encodeUser = Json.encodeToString(
                            Usuario(
                                id = validarUsuario.first().id,
                                email = validarUsuario.first().email,
                                senha = validarUsuario.first().senha,
                                nome = validarUsuario.first().nome,
                                numeroTelefone = validarUsuario.first().numeroTelefone,
                                endereco = validarUsuario.first().endereco
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
        authenticate {
            patch("/adicionarEndereco/{id?}") {
                try {
                    val endereco = call.receive<UsuarioEndereco>()

                    val param = call.parameters["id"] ?: return@patch call.respondText(
                        "Faltando id",
                        status = HttpStatusCode.BadRequest
                    )

                    val findUsuario = eq(Usuario::id.name, param)
                    val update = Updates.push(
                        Usuario::endereco.name, endereco
                    )

                    collection.findOneAndUpdate(findUsuario, update)
                    call.respondText("ok", status = HttpStatusCode.OK)

                } catch (e: Exception) {
                    call.respondText("${e.message}")
                }
            }
        }

        patch("/atualizarCarrinho") {
            try {

            } catch (e: Exception) {
                call.respondText("${e.message}")
            }
        }
    }
}

