package com.cafeteria.models

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioEndereco(
    val id: String,
    val rua: String,
    val cidade: String,
    val estado: String,
    val cep: String,
    val numero: String,
    val complemento: String?
)
