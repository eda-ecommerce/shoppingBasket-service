package eda.shoppingBasket.service.repository

import eda.shoppingBasket.service.entity.ShoppingBasket
import eda.shoppingBasket.service.entity.ShoppingBasketItem
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ShoppingBasketRepository: CrudRepository<ShoppingBasket, UUID>{
    fun findByCustomerID(customerID: UUID): ShoppingBasket

    fun findByShoppingBasketID(shoppingBasketID: UUID): ShoppingBasket

//    was looking at Spring magic ;)

//    fun findByShoppingBasketItems(shoppingBasketItem: ShoppingBasketItem): ShoppingBasket
//
//    fun deleteByShoppingBasketID(shoppingBasketID: UUID)
//
//    fun deleteByCustomerID(customerID: UUID)
//
//    fun deleteByShoppingBasketItems(shoppingBasketItem: ShoppingBasketItem)
//
//    fun deleteAllByShoppingBasketItems(shoppingBasketItem: ShoppingBasketItem)
//
//    fun deleteAllByCustomerID(customerID: UUID)
//
//    fun deleteAllByShoppingBasketID(shoppingBasketID: UUID)
//
//    fun numberOfItemsInShoppingBasket(shoppingBasketID: UUID): Int
//
//    fun modifyItemQuantity(shoppingBasketID: UUID, shoppingBasketItemID: UUID, newQuantity: Int)

}