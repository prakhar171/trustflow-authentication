package com.example.plugins

import com.example.model.UserInformation
import com.example.model.TaskRepository
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization(repository: TaskRepository) {
    install(ContentNegotiation) {
        json()
    }
    routing {
        route("/tasks") {
            post {
                try {
                    val userInformation = call.receive<UserInformation>()
                    if (repository.usernameExists(userInformation.username)) {
                        call.respond(HttpStatusCode.Conflict, "Username already exists")
                    } else {
                        repository.addTask(userInformation)
                        call.respond(HttpStatusCode.NoContent)
                    }
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            delete("/{taskName}") {
                val name = call.parameters["taskName"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}