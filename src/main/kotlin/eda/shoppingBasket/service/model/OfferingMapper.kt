package eda.shoppingBasket.service.model

import eda.shoppingBasket.service.eventing.OfferingEvent
import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.entity.Offering
import java.util.*

class OfferingMapper {
    fun toEntity(offeringDTO: OfferingDTO): Offering{
        return Offering(
            offeringID = offeringDTO.id,
            quantity = offeringDTO.quantity,
            price = offeringDTO.price
        )
    }

    fun toEntity(offeringEvent: OfferingEvent): Offering{
        return Offering(
            offeringID = offeringEvent.id,
            quantity = offeringEvent.quantity,
            price = offeringEvent.price
        )
    }

    fun toDto(offering: Offering): OfferingDTO{
        TODO()
    }
}
