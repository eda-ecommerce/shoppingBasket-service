package eda.shoppingBasket.service.dto

import java.util.UUID

data class ShoppingBasketDTO(
    val id: UUID? = null, //we might need this?
    var shoppingBasketID: UUID,
    var customerID: UUID,
    var totalPrice: Float,
    var totalItemQuantity: Int,
    var shoppingBasketItems: MutableList<ShoppingBasketItemDTO> = mutableListOf(),
) {
}