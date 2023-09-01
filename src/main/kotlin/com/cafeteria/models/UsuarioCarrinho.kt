package com.cafeteria.models

import kotlinx.serialization.Serializable

@Serializable
data class UsuarioCarrinho(
    val id: String, val produto:
    List<Produto>
)
