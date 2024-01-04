package eda.shoppingBasket.service.dto

import java.util.UUID

class ShoppingBasketDTO(
    val shoppingBasketId: UUID,
    val customerId: UUID,
    val totalPrice: Double,
    val totalItemQuantity: Int,
    val items: ArrayList<ShoppingBasketItemDTO>,
) {
}