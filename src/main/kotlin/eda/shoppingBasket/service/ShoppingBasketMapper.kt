package eda.shoppingBasket.service

import eda.shoppingBasket.service.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.entity.ShoppingBasket
import org.springframework.stereotype.Component

@Component
class ShoppingBasketMapper {

    fun toDTO(shoppingBasket: ShoppingBasket): ShoppingBasketDTO {

        return ShoppingBasketDTO(
            shoppingBasketID = shoppingBasket.shoppingBasketID,
            customerID = shoppingBasket.customerID,
            totalPrice = shoppingBasket.totalPrice,
            totalItemQuantity = shoppingBasket.numberOfItemsInShoppingBasket()
            //TODO: we do not have shoppingBasketItems in the constructor of the Entity, so we need to figure out if we want to add it here
        )

    }

    fun toEntity(shoppingBasketDTO: ShoppingBasketDTO): ShoppingBasket {

        return ShoppingBasket(
            shoppingBasketID = shoppingBasketDTO.shoppingBasketID,
            customerID = shoppingBasketDTO.customerID,
            totalPrice = shoppingBasketDTO.totalPrice
        )

    }

}