package eda.shoppingBasket.service.model.dto

import java.util.UUID

data class ShoppingBasketDTO( // ShoppingBasketAnswerDTO
    var shoppingBasketID: UUID? = null,
    var customerID: UUID?,
    var totalPrice: Float = 0.0f,
    var totalItemQuantity: Int = 0,
    var shoppingBasketItems: List<ShoppingBasketItemDTO> = mutableListOf(),
) {
}