package eda.shoppingBasket.service.model

import eda.shoppingBasket.service.model.dto.ShoppingBasketItemDTO
import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import org.springframework.stereotype.Component

@Component
class ShoppingBasketItemMapper {

    fun toDto(shoppingBasketItem: ShoppingBasketItem): ShoppingBasketItemDTO {
        return ShoppingBasketItemDTO(
            offeringId = shoppingBasketItem.offeringID,
            quantity = shoppingBasketItem.quantity,
            totalPrice = shoppingBasketItem.subtotal,
            bundlePrice = shoppingBasketItem.offeringPrice,
            state = shoppingBasketItem.state,
            id = shoppingBasketItem.id
        )
    }

    fun toEntity(shoppingBasketItemDTO: ShoppingBasketItemDTO): ShoppingBasketItem {
        return ShoppingBasketItem(
            quantity = shoppingBasketItemDTO.quantity,
            subtotal = shoppingBasketItemDTO.totalPrice,
            offeringID = shoppingBasketItemDTO.offeringId,
            offeringPrice = shoppingBasketItemDTO.bundlePrice,
            state = shoppingBasketItemDTO.state
        )
    }



}