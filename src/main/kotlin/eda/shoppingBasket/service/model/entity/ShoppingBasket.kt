package eda.shoppingBasket.service.model.entity

import jakarta.persistence.*
import java.util.*

@Entity
class ShoppingBasket(
    val customerID: UUID,
    var totalPrice: Double = 0.0,
    var size: Int = 0,
    @Id
    override val id: UUID = UUID.randomUUID(),
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE,CascadeType.MERGE])
    val items: MutableList<ShoppingBasketItem> = mutableListOf()
): AbstractEntity(){

    constructor(customerID: UUID,
        items: MutableList<ShoppingBasketItem>): this(
        customerID, items.sumOf { it.subtotal }, items.sumOf { it.quantity }, UUID.randomUUID(), items
    ){
            calculateSubtotal()
            this.size = items.sumOf { it.quantity }
        }
    private fun calculateSubtotal(): Double {
        totalPrice = items.sumOf { it.subtotal }
        return totalPrice
    }

    fun addOfferingToBasket(offering: Offering, count: Int){
        val found = items.find { it.offeringID == offering.id }
        if (found != null){
            updateItemQuantity(found.id, found.quantity + count)
            return
        }
        val new = ShoppingBasketItem(
            quantity = count,
            subtotal = offering.price * count,
            offeringID = offering.id,
            offeringPrice = offering.price
        )
        items.add(new)
        totalPrice+=new.subtotal
        size+=new.quantity
    }

    //Should this take offering or Item / Offering ID?
    fun removeItemFromBasket(itemId: UUID){
        val found = items.find { it.id == itemId }
        if(found != null){
            items.remove(found)
            // Do I need calculateSubtotal here?
            totalPrice-=found.subtotal
            size-=found.quantity
        }
        else{
            throw IllegalArgumentException("Item not found")
        }
    }

    fun updateItemQuantity(itemId: UUID, newQuantity: Int){
        val found = items.find { it.id == itemId }
        if(found != null){
            found.quantity = newQuantity
            found.subtotal = found.offeringPrice * newQuantity
            size = items.sumOf { it.quantity }
            calculateSubtotal()
        }
        else{
            throw IllegalArgumentException("Item not found")
        }
    }

    fun readyToCheckout(): Boolean{
        items.forEach { if(it.state == ItemState.UNAVAILABLE) return false }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoppingBasket

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}