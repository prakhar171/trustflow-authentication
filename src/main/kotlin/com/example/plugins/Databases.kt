package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Application.configureDatabases(): Database {
    return Database.connect(
//        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        url = "jdbc:mysql://localhost:3306/information",
        user = "root",
//        driver = "org.h2.Driver",
        driver = "com.mysql.cj.jdbc.Driver",
        password = "",
    )
}