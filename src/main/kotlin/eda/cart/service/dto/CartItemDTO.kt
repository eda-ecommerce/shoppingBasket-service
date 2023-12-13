package eda.cart.service.dto

data class CartItemDTO (
    var productID: Long,
    var quantity: Int = 0,
    var unitPrice: Double
        )