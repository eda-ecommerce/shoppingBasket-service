package eda.shoppingBasket.service.model.dto

import java.util.*

data class ShoppingBasketItemCreationDTO(
    var offeringId: UUID,
    var quantity: Int
)