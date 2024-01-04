package eda.shoppingBasket.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
class Offering(
    val name: String,
    val unitPrice: Double,
    val totalPrice: Double,
    @Id
    val offeringID: UUID,
    @ManyToOne
    val product: Product
) {
}