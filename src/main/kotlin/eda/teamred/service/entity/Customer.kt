package eda.teamred.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextULong

@Entity
class Customer(
    val firstName: String,
    val lastName: String,
    val address: String,
    @Id
    val id: UUID = UUID.randomUUID()
)
