package com.cafeteria.models

import kotlinx.serialization.Serializable

@Serializable
data class User(val email: String, val senha: String, val id: String)
