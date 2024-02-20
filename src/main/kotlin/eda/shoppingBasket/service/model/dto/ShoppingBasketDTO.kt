package eda.shoppingBasket.service.model.dto

import java.util.UUID

data class ShoppingBasketDTO( // ShoppingBasketAnswerDTO
    var shoppingBasketId: UUID? = null,
    var customerId: UUID?,
    var totalPrice: Float = 0.0f,
    var totalItemQuantity: Int = 0,
    var items: List<ShoppingBasketItemDTO> = mutableListOf()
) {
    override fun hashCode(): Int {
        var result = shoppingBasketId?.hashCode() ?: 0
        result = 31 * result + (customerId?.hashCode() ?: 0)
        result = 31 * result + totalPrice.hashCode()
        result = 31 * result + totalItemQuantity
        result = 31 * result + items.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoppingBasketDTO

        if (shoppingBasketId != other.shoppingBasketId) return false
        if (customerId != other.customerId) return false
        if (totalPrice != other.totalPrice) return false
        if (totalItemQuantity != other.totalItemQuantity) return false
        if (items != other.items) return false

        return true
    }
}