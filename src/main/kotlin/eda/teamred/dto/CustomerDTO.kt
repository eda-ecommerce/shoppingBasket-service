package eda.teamred.dto

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "customers")
data class CustomerDTO (
    @Id
    @GeneratedValue
    val id : UUID,
    val firstName : String,
    val lastName : String,
    val address : String
)