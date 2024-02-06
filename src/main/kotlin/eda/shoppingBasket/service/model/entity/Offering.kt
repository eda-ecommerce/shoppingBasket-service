package eda.shoppingBasket.service.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class Offering(
    val quantity: Int,
    val price: Float,
    @Id
    val offeringID: UUID

) {
//    i've added a ManyToOne in ShoppingBasketItem instead of a OneToMany reference here
//    @OneToMany
//    val shoppingBasketItems: MutableList<ShoppingBasketItem> = mutableListOf()
}