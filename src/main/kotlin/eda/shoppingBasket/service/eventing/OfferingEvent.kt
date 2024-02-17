package eda.shoppingBasket.service.eventing

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

data class OfferingEvent(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("status")
    val status: Status,
    @JsonProperty("quantity")
    val quantity: Int,
    @JsonProperty("price")
    val price: Float,
    @JsonProperty("product")
    val product: Product
) {
    enum class Status {
        ACTIVE, INACTIVE;
        override fun toString(): String {
                return name.lowercase()
        }
    }
    data class Product(
        @JsonProperty("id")
        val id: UUID,
        @JsonProperty("status")
        val status: String
    )
}