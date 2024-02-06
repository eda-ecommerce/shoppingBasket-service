package eda.shoppingBasket.service.model.dto

import java.util.UUID

data class OfferingInBasketDTO(
    val offeringID: UUID,
    val quantity: Int
)