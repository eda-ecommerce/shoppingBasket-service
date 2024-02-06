package eda.shoppingBasket.service.repository

import eda.shoppingBasket.service.model.entity.ShoppingBasket
import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ShoppingBasketRepository: CrudRepository<ShoppingBasket, UUID>{
    fun findByCustomerID(customerID: UUID): ShoppingBasket?
    fun findByShoppingBasketID(shoppingBasketID: UUID): ShoppingBasket?
}