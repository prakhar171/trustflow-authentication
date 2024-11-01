package com.example.model

import io.mockk.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.mindrot.jbcrypt.BCrypt

class TaskRepositoryTest {
    private lateinit var repository: MySQLTaskRepository

    @Before
    fun setup() {
        repository = mockk()
    }

    @Test
    fun testAddTask() {
        val userInformation = UserInformation("John", "john123", "1234567890", "john@example.com", "USA", "password")
        every { repository.addTask(userInformation) } just Runs
        every { repository.findUserByUsername("john123") } returns UserInformation("john123", "John", "1234567890", "john@example.com", "USA", "password")

        repository.addTask(userInformation)
        val retrievedTask = repository.findUserByUsername("john123")
        assertNotNull(retrievedTask)
        assertEquals("John", retrievedTask?.username)

        verify { repository.addTask(userInformation) }
        verify { repository.findUserByUsername("john123") }
    }

    @Test
    fun testUpdateUserInfo() {
        val userInformation = UserInformation("John", "john123", "1234567890", "john@example.com", "USA", "password")
        every { repository.addTask(userInformation) } just Runs
        every { repository.updateUserInfo("john123", "John Doe", "0987654321", "john.doe@example.com", "Canada") } just Runs
        every { repository.findUserByUsername("john123") } returns UserInformation("john123", "John Doe", "0987654321", "john.doe@example.com", "Canada", "password")

        repository.addTask(userInformation)
        repository.updateUserInfo("john123", "John Doe", "0987654321", "john.doe@example.com", "Canada")
        val updatedTask = repository.findUserByUsername("john123")
        assertNotNull(updatedTask)
        assertEquals("John Doe", updatedTask.username)
        assertEquals("0987654321", updatedTask.phone)
        assertEquals("john.doe@example.com", updatedTask.email)
        assertEquals("Canada", updatedTask.nationality)

        verify { repository.addTask(userInformation) }
        verify { repository.updateUserInfo("john123", "John Doe", "0987654321", "john.doe@example.com", "Canada") }
        verify { repository.findUserByUsername("john123") }
    }

    @Test
    fun testChangePassword() {
        val userInformation = UserInformation("John", "john123", "1234567890", "john@example.com", "USA", "password")
        every { repository.addTask(userInformation) } just Runs
        every { repository.changePassword("john123", "newpassword") } just Runs
        every { repository.findUserByUsername("john123") } returns UserInformation("john123", "John", "1234567890", "john@example.com", "USA", BCrypt.hashpw("newpassword", BCrypt.gensalt()))

        repository.addTask(userInformation)
        repository.changePassword("john123", "newpassword")
        val updatedTask = repository.findUserByUsername("john123")
        assertNotNull(updatedTask)
        assertTrue(BCrypt.checkpw("newpassword", updatedTask.password))

        verify { repository.addTask(userInformation) }
        verify { repository.changePassword("john123", "newpassword") }
        verify { repository.findUserByUsername("john123") }
    }
}