package com.example.config

data class AuthConfigs(
    val secret: String,
    val issuer: String,
    val audience: String,
    val myRealm: String
)
