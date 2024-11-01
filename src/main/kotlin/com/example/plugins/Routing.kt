package com.example.plugins

import UserSession
import com.example.model.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.auth.*
import io.ktor.server.sessions.get
import kotlin.text.get

//import kotlinx.html.*
//import Security

fun Application.configureRouting(repository: TaskRepository) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respondRedirect("/static/index.html")
        }
        staticResources("/static", "static")

        post("/login") {

            val credentials = call.receive<Map<String, String>>()
            val username = credentials["username"] ?: ""
            val password = credentials["password"] ?: ""
            if (repository.validateUser(username, password)) {
                call.sessions.set(UserSession(username))
                call.respond(HttpStatusCode.OK, "Login successful")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }

        post("/delete-data") {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val username = session.username
            repository.deleteUser(username)
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK, "User data deleted successfully")
        }

        post("/change-info") {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val info = call.receive<Map<String, String>>()
            val username = session.username
            val name = info["name"] ?: ""
            val phone = info["phone"] ?: ""
            val email = info["email"] ?: ""
            val nationality = info["nationality"] ?: ""

            repository.updateUserInfo(username, name, phone, email, nationality)
            call.respond(HttpStatusCode.OK, "Information updated successfully")
        }

        post("/logout") {
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK, "Logout successful")
        }
    }
}
