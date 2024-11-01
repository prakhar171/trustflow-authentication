package com.example.model
import org.mindrot.jbcrypt.BCrypt

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

import kotlin.text.set

interface TaskRepository {
    fun addTask(userInformation: UserInformation)
    fun usernameExists(username: String): Boolean
    fun validateUser(username: String, password: String): Boolean
    fun findUserByUsername(username: String): UserInformation?
    fun changePassword(username: String, newPassword: String)
    fun updateUserInfo(username: String, name: String, phone: String, email: String, nationality: String)
    fun deleteUser(username: String)
}

class MySQLTaskRepository(private val database: Database) : TaskRepository {

    object Users : Table("task") {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)
        val username = varchar("username", length = 255)
        val phone = varchar("phone", length = 255)
        val email = varchar("email", length = 255)
        val nationality = varchar("nationality", length = 255)
        val password = varchar("password", length = 255)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    override fun updateUserInfo(username: String, name: String, phone: String, email: String, nationality: String) {
        transaction(database) {
            Users.update({ Users.username eq username }) {
                it[Users.name] = name
                it[Users.phone] = phone
                it[Users.email] = email
                it[Users.nationality] = nationality
            }
        }
    }

    override fun deleteUser(username: String) {
        transaction(database) {
            Users.deleteWhere { Users.username eq username }
        }
    }

    override fun findUserByUsername(username: String): UserInformation? = transaction(database) {
        Users.select { Users.username eq username }
            .map {
                UserInformation(
                    name = it[Users.name],
                    username = it[Users.username],
                    phone = it[Users.phone],
                    email = it[Users.email],
                    nationality = it[Users.nationality],
                    password = it[Users.password]
                )
            }
            .singleOrNull()
    }

    override fun validateUser(username: String, password: String): Boolean = transaction(database) {
        val user = Users.select { Users.username eq username }.singleOrNull()
        user != null && BCrypt.checkpw(password, user[Users.password])
    }

    override fun addTask(userInformation: UserInformation) {
        val hashedPassword = BCrypt.hashpw(userInformation.password, BCrypt.gensalt())
        transaction(database) {
            Users.insert {
                it[name] = userInformation.name
                it[username] = userInformation.username
                it[phone] = userInformation.phone
                it[email] = userInformation.email
                it[nationality] = userInformation.nationality
                it[password] = hashedPassword
            }
        }
    }

    override fun changePassword(username: String, newPassword: String) {
        val hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt())
        transaction(database) {
            Users.update({ Users.username eq username }) {
                it[password] = hashedPassword
            }
        }
    }

    override fun usernameExists(username: String): Boolean = transaction(database) {
        Users.select { Users.username eq username }.count() > 0
    }
}