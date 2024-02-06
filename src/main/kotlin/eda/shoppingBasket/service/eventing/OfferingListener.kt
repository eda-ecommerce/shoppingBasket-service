package eda.shoppingBasket.service.eventing

import eda.shoppingBasket.service.application.OfferingService
import org.springframework.stereotype.Component

@Component
class OfferingListener {
    lateinit var offeringService: OfferingService
}