package eda.shoppingBasket.service.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class ShoppingBasketItem (
    var quantity: Int = 0,
    var subtotal: Double,
    @Id
    override val id: UUID = UUID.randomUUID(),
    val offeringID: UUID,
    var offeringPrice: Double,
    var state: ItemState = ItemState.AVAILABLE
): AbstractEntity(){

    fun disableItem(){
        state = ItemState.UNAVAILABLE
    }

    fun enableItem(){
        state = ItemState.AVAILABLE
    }

}