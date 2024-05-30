package eda.shoppingBasket.service.model

import eda.shoppingBasket.service.eventing.offering.OfferingEvent
import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.entity.Offering

class OfferingMapper {
    fun toDto(offering:Offering): OfferingDTO{
        return OfferingDTO(
            id = offering.id,
            status = offering.status,
            quantity = offering.quantity,
            price = offering.price,
            productID = null
        )
    }

    fun toEntity(offeringDTO: OfferingDTO): Offering{
        return Offering(
            id = offeringDTO.id,
            status = offeringDTO.status!!,
            quantity = offeringDTO.quantity,
            price = offeringDTO.price
        )
    }

    fun toEntity(offeringEvent: OfferingEvent): Offering{
        return Offering(
            id = offeringEvent.id,
            status = Offering.Status.ACTIVE,
            quantity = offeringEvent.quantity,
            price = offeringEvent.price.toDouble()
        )
    }
}
