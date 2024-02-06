package eda.shoppingBasket.service.eventing

import org.springframework.context.ApplicationEvent
import java.util.UUID
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class OfferingUnavailableEvent(
    source: JvmType.Object,
    val message: String,
    val offeringId: UUID
): ApplicationEvent(source)
