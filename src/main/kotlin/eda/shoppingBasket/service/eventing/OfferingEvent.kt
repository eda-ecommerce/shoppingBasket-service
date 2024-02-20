package eda.shoppingBasket.service.eventing

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import eda.shoppingBasket.service.model.entity.Offering
import java.util.UUID

data class OfferingEvent(
    val id: UUID,
    val status: Offering.Status,
    val quantity: Int,
    val price: Float,
    val product: Product
) {
    data class Product(
        val id: UUID,
        val status: String
    )
}