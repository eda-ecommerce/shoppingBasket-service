package eda.shoppingBasket.service.eventing.offering

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import eda.shoppingBasket.service.model.entity.Offering
import java.util.UUID


@JsonIgnoreProperties(ignoreUnknown = true)
data class OfferingEvent(
    val id: UUID,
    val status: Offering.Status,
    val quantity: Int,
    val price: Float,
    val operation: String?
)