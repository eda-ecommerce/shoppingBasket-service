package eda.shoppingBasket.service.model.dto

import eda.shoppingBasket.service.model.entity.Offering
import java.util.UUID

//TODO: Reconsider structure because mapping
data class OfferingDTO (
    val id: UUID,
    val productID: UUID?,
    val quantity: Int,
    val price: Double,
    val status: Offering.Status?
): DTO{

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + productID.hashCode()
        result = 31 * result + quantity
        result = 31 * result + price.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OfferingDTO

        if (id != other.id) return false
        if (productID != other.productID) return false
        if (quantity != other.quantity) return false
        if (price != other.price) return false

        return true
    }
}
