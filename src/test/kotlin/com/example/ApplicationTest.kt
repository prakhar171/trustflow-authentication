package com.example

import com.example.model.FakeTaskRepository
import com.example.plugins.configureRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testChangeInfoRoute() = testApplication {
        application {
            configureRouting(FakeTaskRepository())
        }
        val response = client.post("/change-info") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"John Doe","phone":"0987654321","email":"john.doe@example.com","nationality":"Canada"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Information updated successfully", response.bodyAsText())
    }

    @Test
    fun testLoginRoute() = testApplication {
        application {
            configureRouting(FakeTaskRepository())
        }
        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"prakhar171","password":"prakhar171"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Login successful", response.bodyAsText())
    }

    @Test
    fun testLogoutRoute() = testApplication {
        application {
            configureRouting(FakeTaskRepository())
        }
        val response = client.post("/logout")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Logout successful", response.bodyAsText())
    }
}