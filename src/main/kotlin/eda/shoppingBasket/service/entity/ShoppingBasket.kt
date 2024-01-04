package eda.shoppingBasket.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
class ShoppingBasket(
    val customerID: UUID,
    var totalPrice: Double = 0.0,
    @Id
    val shoppingBasketID: UUID = UUID.randomUUID()
) {

}