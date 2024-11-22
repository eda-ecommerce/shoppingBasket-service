package eda.shoppingBasket.service.model.dto

import eda.shoppingBasket.service.model.entity.ItemState
import java.util.*

data class ShoppingBasketItemDTO (
    var offeringId: UUID,
    var quantity: Int,
    var totalPrice: Double,
    var bundlePrice: Double,
    var state: ItemState,
    var id: UUID
): DTO
