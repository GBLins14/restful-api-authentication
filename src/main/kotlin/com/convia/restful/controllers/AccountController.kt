package com.convia.restful.controllers

import com.convia.restful.repositories.UserRepository
import com.convia.restful.schemas.BanAccountSchema
import com.convia.restful.schemas.UserSetRoleSchema
import com.convia.restful.services.RequireAdminService
import com.convia.restful.security.Hash
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/admin/accounts")
class AdminAccountController(private val userRepository: UserRepository, private val requireAdminService: RequireAdminService, private val bcrypt: Hash) {
    @GetMapping(value = ["", "/{id}"])
    fun getAccount(@PathVariable(required = false) id: Long?): ResponseEntity<Any> {
        return if (id != null) {
            val account = userRepository.findById(id)
            if (account.isPresent) {
                ResponseEntity.ok(account.get())
            } else {
                ResponseEntity.notFound().build()
            }
        } else {
            val accounts = userRepository.findAll()
            ResponseEntity.ok(accounts)
        }
    }

    @PatchMapping("/role")
    fun setRole(@RequestBody request: UserSetRoleSchema): ResponseEntity<Any> {
        val accountReq = userRepository.findById(request.id).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Conta não encontrada."))

        if (accountReq.role == request.role) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to "A conta já está com este cargo."))
        }

        accountReq.role = request.role
        userRepository.save(accountReq)

        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "message" to "Cargo setado com sucesso. ID: ${request.id}, Cargo: ${request.role}"
            )
        )
    }

    @PatchMapping("/ban")
    fun banAccount(@RequestBody request: BanAccountSchema): ResponseEntity<Any> {
        val accountReq = userRepository.findById(request.id).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Conta não encontrada."))

        if (request.duration <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "A duração deve ser maior que 0."))
        }

        val now = LocalDateTime.now()
        val banExpiresAt = now.plus(request.duration, request.unit)

        accountReq.banned = true
        accountReq.bannedAt = now
        accountReq.banExpiresAt = banExpiresAt
        userRepository.save(accountReq)

        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "message" to "Conta banida com sucesso. ID: ${request.id}, Duration: ${request.duration}, Unit: ${request.unit}."
            )
        )
    }

    @PatchMapping("/unban/{id}")
    fun banAccount(@PathVariable id: Long): ResponseEntity<Any> {
        val accountReq = userRepository.findById(id).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Conta não encontrada."))

        if (!accountReq.banned) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "A conta não está banida."))
        }

        accountReq.banned = false
        accountReq.bannedAt = null
        accountReq.banExpiresAt = null
        userRepository.save(accountReq)

        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "message" to "Conta desbanida com sucesso. ID: $id"
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delAccount(@PathVariable id: Long): ResponseEntity<Any> {
        val accountReq = userRepository.findById(id).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Conta não encontrada."))

        userRepository.delete(accountReq)

        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "message" to "Conta deletada com sucesso. ID: $id"
            )
        )
    }
}
