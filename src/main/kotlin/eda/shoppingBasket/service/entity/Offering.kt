package eda.shoppingBasket.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
class Offering(
    val name: String,
    val unitPrice: Float,
    val totalPrice: Float,
    @Id
    val offeringID: UUID
) {
//    i've added a ManyToOne in ShoppingBasketItem instead of a OneToMany reference here
//    @OneToMany
//    val shoppingBasketItems: MutableList<ShoppingBasketItem> = mutableListOf()
}