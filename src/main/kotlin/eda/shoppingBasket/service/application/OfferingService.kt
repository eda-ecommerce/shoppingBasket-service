package eda.shoppingBasket.service.application

import eda.shoppingBasket.service.application.exception.OfferingNotFoundException
import eda.shoppingBasket.service.eventing.offering.OfferingAvailableEvent
import eda.shoppingBasket.service.eventing.offering.OfferingEvent
import eda.shoppingBasket.service.eventing.offering.OfferingUnavailableEvent
import eda.shoppingBasket.service.model.OfferingMapper
import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.entity.Offering
import eda.shoppingBasket.service.repository.OfferingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class OfferingService: ApplicationEventPublisherAware {
    @Autowired
    lateinit var offeringRepository: OfferingRepository

    private val offeringMapper: OfferingMapper = OfferingMapper()

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Transactional
    fun saveOffering(offeringEvent: OfferingEvent){
        offeringRepository.save(offeringMapper.toEntity(offeringEvent))
        applicationEventPublisher.publishEvent(OfferingAvailableEvent(this, offeringEvent.id))
    }

    @Transactional
    fun saveOffering(offeringDTO: OfferingDTO): OfferingDTO{
        val saved = offeringRepository.save(offeringMapper.toEntity(offeringDTO))
        applicationEventPublisher.publishEvent(OfferingAvailableEvent(this, offeringDTO.id))
        return offeringMapper.toDto(saved)
    }

    @Transactional
    fun disableOffering(offeringId: UUID){
        applicationEventPublisher.publishEvent(OfferingUnavailableEvent(this, offeringId))
        offeringRepository.deleteById(offeringId)
    }

    fun getOffering(uuid: UUID): Offering {
        return offeringRepository.findByIdOrNull(uuid)?: throw OfferingNotFoundException()
    }

    fun getAllOfferings(): List<OfferingDTO> {
        return offeringRepository.findAll().map { offeringMapper.toDto(it) }
    }

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher
    }

}