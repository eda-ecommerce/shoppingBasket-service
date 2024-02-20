package eda.shoppingBasket.service.model

import eda.shoppingBasket.service.model.dto.ShoppingBasketItemDTO
import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import org.springframework.stereotype.Component

@Component
class ShoppingBasketItemMapper {

    fun toDto(shoppingBasketItem: ShoppingBasketItem): ShoppingBasketItemDTO {
        return ShoppingBasketItemDTO(
            shoppingBasketItemId = shoppingBasketItem.shoppingBasketItemID,
            shoppingBasketId = shoppingBasketItem.shoppingBasket.shoppingBasketID,
            offeringId = shoppingBasketItem.offeringID,
            quantity = shoppingBasketItem.quantity,
            totalPrice = shoppingBasketItem.totalPrice,
            itemState = shoppingBasketItem.state
        )
    }

}