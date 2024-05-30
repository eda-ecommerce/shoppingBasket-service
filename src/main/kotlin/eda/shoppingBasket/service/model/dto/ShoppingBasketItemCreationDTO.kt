package eda.shoppingBasket.service.model.dto

import eda.shoppingBasket.service.model.entity.ItemState
import java.util.*

data class ShoppingBasketItemCreationDTO(
    var offeringId: UUID,
    var quantity: Int
)