package eda.shoppingBasket.service.model.dto

import java.util.UUID

data class ShoppingBasketDTO (
    var customerId: UUID,
    var totalPrice: Double,
    var size: Int,
    var id: UUID,
    var items: List<ShoppingBasketItemDTO>
): DTO