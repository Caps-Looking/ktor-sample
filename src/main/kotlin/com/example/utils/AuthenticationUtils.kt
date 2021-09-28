package com.example.utils

import com.example.models.User
import com.mongodb.client.MongoDatabase
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

class AuthenticationUtils {
    companion object {
        fun getUserId(call: ApplicationCall, database: MongoDatabase): String? {
            val username = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
            return database.getCollection<User>().findOne { User::username eq username }?.id
        }
    }
}