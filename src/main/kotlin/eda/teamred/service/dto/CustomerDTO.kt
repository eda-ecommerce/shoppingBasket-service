package eda.teamred.service.dto

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

data class CustomerDTO (
    val firstName : String,
    val lastName : String,
    val address : String
)
