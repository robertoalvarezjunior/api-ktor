package com.cafeteria.routes

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.routing.*

fun Route.cadastroRouting(database: MongoDatabase) {
    route("/cadastro") {
        post { }
    }
}