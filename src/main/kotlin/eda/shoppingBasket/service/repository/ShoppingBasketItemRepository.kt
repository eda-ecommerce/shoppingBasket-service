package eda.shoppingBasket.service.repository

import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ShoppingBasketItemRepository: CrudRepository<ShoppingBasketItem, UUID>{
    fun findAllByOfferingID(offerID: UUID): List<ShoppingBasketItem>
}