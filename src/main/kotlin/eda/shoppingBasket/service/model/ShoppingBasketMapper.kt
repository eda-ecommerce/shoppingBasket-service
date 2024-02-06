package eda.shoppingBasket.service.model

import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketItemDTO
import eda.shoppingBasket.service.model.entity.ShoppingBasket
import org.springframework.stereotype.Component
import java.util.*

@Component
class ShoppingBasketMapper {

    fun toDTO(shoppingBasket: ShoppingBasket, shoppingBasketItems: List<ShoppingBasketItemDTO>): ShoppingBasketDTO {

        return ShoppingBasketDTO(
            shoppingBasketID = shoppingBasket.shoppingBasketID,
            customerID = shoppingBasket.customerID,
            totalPrice = shoppingBasketItems.map { it.totalPrice }.sum(),
            totalItemQuantity = shoppingBasketItems.size,
            shoppingBasketItems = shoppingBasketItems)
    }

    fun toEntity(shoppingBasketDTO: ShoppingBasketDTO): ShoppingBasket {
        return ShoppingBasket(
            shoppingBasketID = shoppingBasketDTO.shoppingBasketID?: UUID.randomUUID(),
            customerID = shoppingBasketDTO.customerID?: UUID.randomUUID(),
            totalPrice = shoppingBasketDTO.totalPrice
        )

    }

}