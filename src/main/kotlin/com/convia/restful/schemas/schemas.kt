package com.convia.restful.schemas

import com.convia.restful.models.Role
import java.time.temporal.ChronoUnit

data class UserSignUpSchema(
    val cpf: String,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val password: String
)

data class UserSignInSchema(
    val login: String,
    val password: String
)

data class BanAccountSchema(
    val id: Long,
    val duration: Long,
    val unit: ChronoUnit
)

data class UserSetRoleSchema(
    val id: Long,
    val role: Role
)