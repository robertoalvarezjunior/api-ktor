package com.cafeteria.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String, val email: String, val senha: String, val nome: String,
    val numeroTelefone: String,
    val endereco: List<UsuarioEndereco>?,
    val carrinho: UsuarioCarrinho?,
)
