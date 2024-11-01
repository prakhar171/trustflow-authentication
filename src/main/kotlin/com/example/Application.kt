package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import com.example.model.FakeTaskRepository
import com.example.model.MySQLTaskRepository
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Database
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import io.ktor.server.http.content.static
import io.ktor.server.http.content.resources
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }

    routing {
        static("/static") {
            resources("static")
        }
        // other routes
    }
//    configureDatabases()
    val database = configureDatabases()
    val repository = MySQLTaskRepository(database)
    configureSecurity(repository)

    configureRouting(repository)
    configureSerialization(repository)

}
