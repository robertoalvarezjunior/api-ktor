package com.cafeteria

import com.cafeteria.databases.database
import com.cafeteria.plugins.configureRouting
import com.cafeteria.plugins.configureSecurity
import com.cafeteria.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureRouting(database)
}
