package eda.shoppingBasket.service.model.dto

import java.util.UUID

data class ShoppingBasketDTO( // ShoppingBasketAnswerDTO
    var shoppingBasketId: UUID? = null,
    var customerId: UUID?,
    var totalPrice: Float = 0.0f,
    var totalItemQuantity: Int = 0,
    var items: List<ShoppingBasketItemDTO> = mutableListOf(),
) {
}