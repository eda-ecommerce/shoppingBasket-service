package eda.shoppingBasket.service.model.dto

import java.util.UUID

data class OfferingDTO (
    val id: UUID,
    val productID: UUID,
    val quantity: Int,
    val price: Float,
)
