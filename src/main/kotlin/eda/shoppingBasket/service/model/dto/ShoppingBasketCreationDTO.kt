package eda.shoppingBasket.service.model.dto

import java.util.*

data class ShoppingBasketCreationDTO(
    var customerId: UUID,
    var items: List<ShoppingBasketItemCreationDTO>
)
