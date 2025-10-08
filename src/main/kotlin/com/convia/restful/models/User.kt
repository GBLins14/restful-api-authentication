package com.convia.restful.models

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import jakarta.persistence.*

enum class Role {
    USER,
    CADMIN
}

enum class Plan {
    FREE,
    STANDARD,
    PREMIUM
}

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var cpf: String = "",

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false, unique = true)
    var username: String = "",

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(nullable = false, unique = true)
    var phone: String = "",

    @JsonIgnore
    @Column(name = "hash_password", nullable = false)
    var hashedPassword: String = "",

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var plan: Plan = Plan.FREE,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER,

    @Column(nullable = false)
    var banned: Boolean = false,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var bannedAt: LocalDateTime? = null,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var banExpiresAt: LocalDateTime? = null,

    @Column(nullable = false)
    var failedLoginAttempts: Int = 0,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
