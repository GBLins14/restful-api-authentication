package com.convia.restful.controllers

import com.convia.restful.models.Role
import com.convia.restful.models.User
import com.convia.restful.repositories.UserRepository
import com.convia.restful.schemas.UserSignInSchema
import com.convia.restful.schemas.UserSignUpSchema
import com.convia.restful.security.Hash
import com.convia.restful.security.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/auth")
class AccountController(private val userRepository: UserRepository, private val jwtUtil: JwtUtil, private val bcrypt: Hash) {
    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: UserSignUpSchema): ResponseEntity<Any> {
        val existingCpf = userRepository.findByCpf(request.cpf)
        val existingUsername = userRepository.findByUsername(request.username)
        val existingEmail = userRepository.findByEmail(request.email)
        val existingPhone = userRepository.findByPhone(request.phone)

        if (existingCpf != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to "Já existe uma conta registrada com este número de CPF."))
        }
        if (existingUsername != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to "Já existe uma conta registrada com este nome de usuário."))
        }
        if (existingEmail != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to "Já existe uma conta registrada com este endereço de email."))
        }
        if (existingPhone != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to "Já existe uma conta registrada com este número de telefone."))
        }

        val user = User(
            cpf = request.cpf,
            name = request.name,
            username = request.username,
            email = request.email,
            phone = request.phone,
            hashedPassword = bcrypt.encodePassword(request.password)
        )

        val account = userRepository.save(user)

        return ResponseEntity.status(HttpStatus.CREATED).body(account)
    }

    @PostMapping("/sign-in")
    fun signIn(@RequestBody request: UserSignInSchema): ResponseEntity<Any> {
        val user = userRepository.findByUsernameOrEmail(request.login, request.login)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Usuário não encontrado"))

        if (user.banned && user.banExpiresAt?.isAfter(LocalDateTime.now()) == true) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Conta temporariamente bloqueada. Tente novamente mais tarde."))
        } else if (user.banned) {
            user.banned = false
            user.bannedAt = null
            user.banExpiresAt = null
            user.failedLoginAttempts = 0
            userRepository.save(user)
        }

        if (!bcrypt.checkPassword(request.password, user.hashedPassword)) {
            val failedLoginAttempts = user.failedLoginAttempts
            if (failedLoginAttempts >= 3) {
                val now = LocalDateTime.now()
                user.banned = true
                user.bannedAt = now
                user.banExpiresAt = now.plusMinutes(5)
                userRepository.save(user)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Conta bloqueada devido a tentativas excessivas."))
            }
            user.failedLoginAttempts += 1
            userRepository.save(user)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Senha incorreta."))
        }

        user.failedLoginAttempts = 0
        userRepository.save(user)

        val token = jwtUtil.generateToken(user.username)
        return ResponseEntity.ok(mapOf("token" to token))
    }
}