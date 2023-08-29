package com.cafeteria

import com.cafeteria.plugins.configureRouting
import com.cafeteria.plugins.configureSecurity
import com.cafeteria.plugins.configureSerialization
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val uri = "mongodb+srv://${System.getenv("mongoUsuario")}:${
        System.getenv("mongoSenha")
    }@cafeteriadb" +
            ".fi1b8bj" +
            ".mongodb" +
            ".net/?retryWrites=true&w=majority"
    val mongoClient = MongoClient.create(uri)
    val database = mongoClient.getDatabase("cafeteriaDB")

    configureSerialization()
    configureSecurity()
    configureRouting(database)
}
