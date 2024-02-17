package eda.shoppingBasket.service.eventing

import java.util.UUID

data class OfferingEvent(
    val id: UUID,
    val status: Status,
    val quantity: Int,
    val price: Float,
    val product: Product
) {
    enum class Status {
        ACTIVE, INACTIVE;
        override fun toString(): String {
                return name.lowercase()
        }
    }
    data class Product(
        val id: UUID,
        val status: String
    )
}