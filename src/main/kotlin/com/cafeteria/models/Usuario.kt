package com.cafeteria.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val idUsuario: String?,
    val email: String,
    val senha: String,
    val nome: String,
    val numeroTelefone: String,
)
