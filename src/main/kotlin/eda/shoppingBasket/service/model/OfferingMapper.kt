package eda.shoppingBasket.service.model

import eda.shoppingBasket.service.eventing.offering.OfferingEvent
import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.entity.Offering

class OfferingMapper {
    fun toEntity(offeringDTO: OfferingDTO): Offering{
        return Offering(
            offeringID = offeringDTO.id,
            quantity = offeringDTO.quantity,
            price = offeringDTO.price,
        )
    }

    fun toEntity(offeringEvent: OfferingEvent): Offering{
        return Offering(
            offeringID = offeringEvent.id,
            quantity = offeringEvent.quantity,
            price = offeringEvent.price,
        )
    }

    fun toDto(offering: Offering): OfferingDTO{
        return OfferingDTO(
            id = offering.offeringID,
            quantity = offering.quantity,
            price = offering.price,
            productID = null,
            status = offering.status
        )
    }
}
