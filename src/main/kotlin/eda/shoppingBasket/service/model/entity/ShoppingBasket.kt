package eda.shoppingBasket.service.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
class ShoppingBasket(
    var customerID: UUID,
    var totalPrice: Float = 0.0f,
    @Id
    val shoppingBasketID: UUID = UUID.randomUUID(),
)