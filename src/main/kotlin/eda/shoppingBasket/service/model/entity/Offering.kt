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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Offering

        return offeringID == other.offeringID
    }

    override fun hashCode(): Int {
        var result = quantity
        result = 31 * result + price.hashCode()
        result = 31 * result + offeringID.hashCode()
        return result
    }
//    i've added a ManyToOne in ShoppingBasketItem instead of a OneToMany reference here
//    @OneToMany
//    val shoppingBasketItems: MutableList<ShoppingBasketItem> = mutableListOf()
}