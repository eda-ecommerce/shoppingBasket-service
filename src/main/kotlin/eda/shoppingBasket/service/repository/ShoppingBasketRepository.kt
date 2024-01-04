package eda.shoppingBasket.service.repository

import eda.shoppingBasket.service.entity.ShoppingBasket
import eda.shoppingBasket.service.entity.ShoppingBasketItem
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ShoppingBasketRepository: CrudRepository<ShoppingBasket, UUID>{

}