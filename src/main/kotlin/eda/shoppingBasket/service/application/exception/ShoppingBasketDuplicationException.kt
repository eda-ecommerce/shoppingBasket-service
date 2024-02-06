package eda.shoppingBasket.service.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason= "Shopping Basket already exists")
class ShoppingBasketDuplicationException(message: String = "Shopping Basket already exists") : Exception(message) {
}