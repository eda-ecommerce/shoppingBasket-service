package eda.shoppingBasket.service.eventing.offering

import org.springframework.context.ApplicationEvent
import java.util.*

class OfferingUnavailableEvent(
    source: Any,
    val offeringId: UUID
): ApplicationEvent(source)
