package eda.shoppingBasket.service.repository

import eda.shoppingBasket.service.entity.ShoppingBasket
import eda.shoppingBasket.service.entity.ShoppingBasketItem
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ShoppingBasketItemRepository: CrudRepository<ShoppingBasketItem, UUID>{
    fun findByShoppingBasket(shoppingBasket: ShoppingBasket): List<ShoppingBasketItem>
    fun findAllByItemPrice_AndQuantity(itemPrice: Double, quantity: Int): Collection<ShoppingBasketItem>
}