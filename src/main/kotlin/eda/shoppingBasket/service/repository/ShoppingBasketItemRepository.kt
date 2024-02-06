package eda.shoppingBasket.service.repository

import eda.shoppingBasket.service.model.entity.ShoppingBasket
import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ShoppingBasketItemRepository: CrudRepository<ShoppingBasketItem, UUID>{
    fun findByShoppingBasket(shoppingBasket: ShoppingBasket): List<ShoppingBasketItem>
    fun findByShoppingBasketAndOfferingID(shoppingBasket: ShoppingBasket, offeringID: UUID): ShoppingBasketItem?
    fun findByShoppingBasketAndShoppingBasketItemID(shoppingBasket: ShoppingBasket, shoppingBasketItemID: UUID): ShoppingBasketItem?
}