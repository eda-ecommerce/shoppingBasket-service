package eda.shoppingBasket.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
class ShoppingBasket(
    val customerID: UUID,
    var totalPrice: Float = 0.0f,
    @Id
    val shoppingBasketID: UUID = UUID.randomUUID()
) {
    //TODO: remove the bidirectional reference.
    @OneToMany
    val shoppingBasketItems: MutableList<ShoppingBasketItem> = mutableListOf()

    fun getShoppingBasketTotal(): Float = totalPrice

    fun numberOfItemsInShoppingBasket(): Int = shoppingBasketItems.size

}