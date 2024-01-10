package eda.shoppingBasket.service.dto

import java.util.UUID

data class ShoppingBasketItemDTO (
    val id: UUID? = null, //we might need this?
    val shoppingBasketID: UUID,
    val offeringID: UUID,
    val offeringAmount: Int,
    val totalPrice: Float
        )