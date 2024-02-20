package eda.shoppingBasket.service.model.dto

import eda.shoppingBasket.service.model.entity.ItemState
import java.util.UUID

data class ShoppingBasketItemDTO (
    val shoppingBasketItemId: UUID,
    val shoppingBasketId: UUID,
    val offeringId: UUID,
    val quantity: Int,
    val totalPrice: Float,
    val itemState: ItemState
)