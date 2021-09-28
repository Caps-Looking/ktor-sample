package com.example.routes

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.AuthConfigs
import com.example.models.User
import com.mongodb.client.MongoDatabase
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import java.util.*

fun Application.registerAuthenticationRoutes(configs: AuthConfigs, database: MongoDatabase) {
    val collection = database.getCollection<User>()

    routing {
        post("/login") {
            val user = call.receive<User>()

            val dbUser = collection.findOne(User::username eq user.username) ?: return@post call.respondText(
                "User not found",
                status = HttpStatusCode.NotFound
            )

            if (!BCrypt.verifyer().verify(user.password.toCharArray(), dbUser.password).verified) {
                return@post call.respondText("Wrong password!", status = HttpStatusCode.Forbidden)
            }

            val token = JWT.create()
                .withAudience(configs.audience)
                .withIssuer(configs.issuer)
                .withClaim("username", user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 3600000))
                .sign(Algorithm.HMAC256(configs.secret))

            call.respond(hashMapOf("token" to token))
        }

        post("/register") {
            val user = call.receive<User>()

            if (collection.findOne(User::username eq user.username) != null) {
                return@post call.respondText("This username was already taken", status = HttpStatusCode.BadRequest)
            }

            val password = BCrypt.withDefaults().hashToString(5, user.password.toCharArray())
            val insertUser = User(username = user.username, password = password)
            collection.insertOne(insertUser)
            call.respondText("User created correctly", status = HttpStatusCode.Created)
        }
    }
}