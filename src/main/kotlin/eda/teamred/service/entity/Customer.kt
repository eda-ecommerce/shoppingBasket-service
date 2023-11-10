package eda.teamred.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
class Customer(
    val firstName: String,
    val lastName: String,
    val address: String,
    @Id
    val id: UUID = UUID.randomUUID()){
}
