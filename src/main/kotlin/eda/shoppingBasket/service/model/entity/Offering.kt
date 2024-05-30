package eda.shoppingBasket.service.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class Offering(
    val quantity: Int,
    val price: Double,
    val status: Status = Status.ACTIVE,
    @Id
    override val id: UUID = UUID.randomUUID()
): AbstractEntity() {
    enum class Status {
        ACTIVE, INACTIVE, RETIRED;
        override fun toString(): String {
            return name.lowercase()
        }
    }
}