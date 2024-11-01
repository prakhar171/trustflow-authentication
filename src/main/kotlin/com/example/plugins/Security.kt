package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import UserSession
import com.example.model.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.thymeleaf.ThymeleafContent

import kotlin.reflect.KClass
import kotlin.text.clear
import kotlin.text.get

fun Application.configureSecurity(repository: TaskRepository) {
    install(Sessions) {
        cookie<UserSession>("auth-session")
    }

    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                if (session.username.isNotEmpty()) session else null
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }

    routing {
        post("/login") {
            try {
                val credentials = call.receive<Map<String, String>>()
                val username = credentials["username"] ?: ""
                val password = credentials["password"] ?: ""

                if (repository.validateUser(username, password)) {
                    call.sessions.set(UserSession(username))
                    call.respondRedirect("/user")
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                }
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        authenticate("auth-session") {
            post("/change-password") {
                val session = call.sessions.get<UserSession>()
                if (session != null) {
                    val params = call.receiveParameters()
                    val oldPassword = params["oldPassword"] ?: ""
                    val newPassword = params["newPassword"] ?: ""
                    val confirmPassword = params["confirmPassword"] ?: ""

                    if (newPassword != confirmPassword) {
                        call.respond(HttpStatusCode.BadRequest, "New passwords do not match")
                        return@post
                    }

                    if (repository.validateUser(session.username, oldPassword)) {
                        repository.changePassword(session.username, newPassword)
                        call.respondRedirect("/user?message=Password+changed+successfully")
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Old password is incorrect")
                    }
                } else {
                    call.respondRedirect("/login")
                }
            }

            get("/user") {
                val session = call.sessions.get<UserSession>()
                if (session != null) {
                    val user = repository.findUserByUsername(session.username)
                    if (user != null) {
                        call.respond(ThymeleafContent("user", mapOf(
                            "username" to user.username,
                            "name" to user.name,
                            "phone" to user.phone,
                            "email" to user.email,
                            "nationality" to user.nationality
                        )))
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } else {
                    call.respondRedirect("/login")
                }
            }
            post("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/static/index.html?message=session+successfully+ended")
            }
        }
    }
}