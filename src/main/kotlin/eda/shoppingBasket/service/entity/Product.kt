package eda.shoppingBasket.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class Product(
    val unitPrice: Double,
    val name: String,
    @Id
    val productID: UUID
) {
}