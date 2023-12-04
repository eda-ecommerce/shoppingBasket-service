package eda.teamred.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID
import kotlin.random.Random
import kotlin.random.nextULong

@Entity
class Customer(
    var firstName: String = "",
    var lastName: String = "",
    var address: String = "",
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID()
){
}
