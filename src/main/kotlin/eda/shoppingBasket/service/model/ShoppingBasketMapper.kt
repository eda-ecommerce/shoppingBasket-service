package eda.shoppingBasket.service.model

import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.model.entity.ShoppingBasket
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ShoppingBasketMapper {

    @Autowired
    lateinit var shoppingBasketItemMapper: ShoppingBasketItemMapper

    fun toDTO(shoppingBasket: ShoppingBasket): ShoppingBasketDTO {
        return ShoppingBasketDTO(
            customerId = shoppingBasket.customerID,
            totalPrice = shoppingBasket.totalPrice,
            size = shoppingBasket.size,
            id = shoppingBasket.id,
            items = shoppingBasket.items.map { shoppingBasketItemMapper.toDto(it) }
        )
    }

    fun toEntity(shoppingBasketDTO: ShoppingBasketDTO): ShoppingBasket {

        return ShoppingBasket(
            customerID = shoppingBasketDTO.customerId,
            totalPrice = shoppingBasketDTO.totalPrice,
            size = shoppingBasketDTO.size,
            items = shoppingBasketDTO.items.map { shoppingBasketItemMapper.toEntity(it) }.toMutableList(),
        )
    }

}