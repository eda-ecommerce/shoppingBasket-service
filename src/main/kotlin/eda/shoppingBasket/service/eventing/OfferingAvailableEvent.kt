package eda.shoppingBasket.service.eventing

import org.springframework.context.ApplicationEvent
import java.util.*

class OfferingAvailableEvent(
    source: Any,
    val offeringId: UUID
): ApplicationEvent(source)