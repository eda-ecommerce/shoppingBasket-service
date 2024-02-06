package eda.shoppingBasket.service.model.dto

import java.util.UUID

data class OfferingDTO (
    val id: UUID,
    val status: String,
    val quantity: Int,
    val price: Float,
)
