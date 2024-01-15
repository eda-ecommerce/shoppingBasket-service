package eda.shoppingBasket.service.eventing.offering

import eda.shoppingBasket.service.repository.OfferingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OfferingConsumer {
    @Autowired
    lateinit var offeringRepository: OfferingRepository

    fun consumeOfferingEvent(event: OfferingEvent) {
        when (event.eventType) {
            EventType.OFFERING_CREATED -> {
                offeringRepository.save(event.dto.toOffering())
            }
            EventType.OFFERING_UPDATED -> {
                offeringRepository.save(event.dto.toOffering())
            }
            EventType.OFFERING_DELETED -> {
                offeringRepository.deleteById(event.dto.id)
            }
        }
    }
}