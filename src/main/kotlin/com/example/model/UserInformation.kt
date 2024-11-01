package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInformation(
    val name: String,
    val username: String,
    val phone: String,
    val email: String,
    val nationality: String,
    val password: String,
)