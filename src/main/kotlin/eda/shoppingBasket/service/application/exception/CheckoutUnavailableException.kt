package eda.shoppingBasket.service.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
class CheckoutUnavailableException(message: String): Exception(message) {
}