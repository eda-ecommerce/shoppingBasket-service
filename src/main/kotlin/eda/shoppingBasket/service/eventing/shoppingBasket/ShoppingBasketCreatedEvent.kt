package eda.shoppingBasket.service.eventing.shoppingBasket

import eda.shoppingBasket.service.model.dto.DTO
import org.springframework.context.ApplicationEvent

class ShoppingBasketCreatedEvent(source: Any, val payload: DTO): ApplicationEvent(source)