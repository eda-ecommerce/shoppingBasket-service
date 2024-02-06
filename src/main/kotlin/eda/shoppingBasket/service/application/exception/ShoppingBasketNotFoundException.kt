package eda.shoppingBasket.service.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Shopping Basket not found")
class ShoppingBasketNotFoundException(message : String = "Shopping Basket not found") : Exception(message) {
}

