package eda.shoppingBasket.service.dto

import java.util.UUID

data class ShoppingBasketItemDTO (
    val offeringID: UUID,
    val offeringAmount: Int,
    val totalPrice: Double
        )