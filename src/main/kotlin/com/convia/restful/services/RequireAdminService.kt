package com.convia.restful.services

import com.convia.restful.models.Role
import com.convia.restful.repositories.UserRepository
import com.convia.restful.security.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class RequireAdminService(private val userRepository: UserRepository, private val jwtUtil: JwtUtil) {
    fun validateAdmin(authHeader: String?): ResponseEntity<Any>? {
        if (authHeader.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Header Authorization não enviado."))
        }

        val token = authHeader.removePrefix("Bearer ").trim()
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Token JWT vazio."))
        }

        val username = try {
            jwtUtil.getUsername(token)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Token inválido ou expirado."))
        }

        val account = userRepository.findByUsername(username)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Conta não encontrada."))

        if (account.role != Role.CADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Você não é um administrador."))
        }

        return null
    }

}
