package eda.shoppingBasket.service.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason= "Shopping Basket Item not found")
class ShoppingBasketItemNotFoundException(message : String = "Shopping Basket Item not found") : Exception(message) {
}