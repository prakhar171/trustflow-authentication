package com.example.model
import org.mindrot.jbcrypt.BCrypt


class FakeTaskRepository : TaskRepository {
    private val userInformations = mutableListOf(
        UserInformation("Prakhar", "prakhar171", "0626393548", "prakhar171@gmail.com", "Indian", BCrypt.hashpw("prakhar171", BCrypt.gensalt())),
    )

    override fun changePassword(username: String, newPassword: String) {
        val task = userInformations.find { it.username == username }
        task?.let {
            val updatedTask = it.copy(password = BCrypt.hashpw(newPassword, BCrypt.gensalt()))
            userInformations[userInformations.indexOf(it)] = updatedTask
        }
    }

    override fun deleteUser(username: String) {
        userInformations.removeIf { it.username == username }
    }

    override fun updateUserInfo(username: String, name: String, phone: String, email: String, nationality: String) {
        val task = userInformations.find { it.username == username }
        task?.let {
            val updatedTask = it.copy(name = name, phone = phone, email = email, nationality = nationality)
            userInformations[userInformations.indexOf(it)] = updatedTask
        }
    }

    override fun addTask(userInformation: UserInformation) {
        val hashedTask = userInformation.copy(password = BCrypt.hashpw(userInformation.password, BCrypt.gensalt()))
        userInformations.add(hashedTask)
    }

    override fun usernameExists(username: String): Boolean {
        return userInformations.any { it.username == username }
    }

    override fun validateUser(username: String, password: String): Boolean {
        val task = userInformations.find { it.username == username }
        return task != null && BCrypt.checkpw(password, task.password)
    }

    override fun findUserByUsername(username: String): UserInformation? {
        val task = userInformations.find { it.username == username }
        return task?.let {
            UserInformation(
                username = it.username,
                name = it.name,
                phone = it.phone,
                email = it.email,
                nationality = it.nationality,
                password = it.password
            )
        }
    }
}