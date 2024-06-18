package eda.shoppingBasket.service.application

import eda.shoppingBasket.service.application.exception.CheckoutUnavailableException
import eda.shoppingBasket.service.application.exception.ShoppingBasketDuplicationException
import eda.shoppingBasket.service.application.exception.ShoppingBasketNotFoundException
import eda.shoppingBasket.service.eventing.shoppingBasket.SBOperation
import eda.shoppingBasket.service.eventing.shoppingBasket.ShoppingBasketProducer
import eda.shoppingBasket.service.eventing.shoppingBasket.ShoppingBasketCreatedEvent
import eda.shoppingBasket.service.model.ShoppingBasketMapper
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.model.entity.ShoppingBasket
import eda.shoppingBasket.service.repository.ShoppingBasketRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShoppingBasketService: ApplicationEventPublisherAware {

    @Autowired
    lateinit var shoppingBasketMapper: ShoppingBasketMapper

    @Autowired
    lateinit var shoppingBasketRepository: ShoppingBasketRepository

    @Autowired
    lateinit var offeringService: OfferingService

    @Autowired
    private lateinit var producer: ShoppingBasketProducer

    @Autowired
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    final val logger = LoggerFactory.getLogger(ShoppingBasketService::class.java)

    fun getShoppingBasket(shoppingBasketID: UUID): ShoppingBasketDTO {
        return shoppingBasketMapper.toDTO(
        shoppingBasketRepository.findByIdOrNull(shoppingBasketID)
            ?: throw ShoppingBasketNotFoundException()
        )
    }

    fun getShoppingBasketByCustomerID(customerID: UUID): ShoppingBasketDTO {
        return shoppingBasketMapper.toDTO(
            shoppingBasketRepository.findByCustomerID(customerID)
                ?: throw ShoppingBasketNotFoundException()
        )
    }

    fun createShoppingBasket(shoppingBasketDTO: ShoppingBasketDTO): ShoppingBasketDTO{
        val newFull = shoppingBasketMapper.toEntity(shoppingBasketDTO)
        val newSanitized = ShoppingBasket(newFull.customerID, newFull.items)
        val found = shoppingBasketRepository.findByIdOrCustomerID(newSanitized.id, newSanitized.customerID)
        if(found != null){
            throw ShoppingBasketDuplicationException()
        }
        shoppingBasketRepository.save(newSanitized)
        val dto = shoppingBasketMapper.toDTO(newSanitized)
        applicationEventPublisher.publishEvent(ShoppingBasketCreatedEvent(this, dto))
        producer.sendMessage(dto, SBOperation.CREATED)
        return dto
    }

    fun addOfferingToBasket(shoppingBasketID: UUID, offeringID: UUID, count: Int): ShoppingBasketDTO{
        val found = shoppingBasketRepository.findByIdOrNull(shoppingBasketID) ?: throw ShoppingBasketNotFoundException()
        val offering = offeringService.getOffering(offeringID)
        found.addOfferingToBasket(offering, count)
        shoppingBasketRepository.save(found)
        producer.sendMessage(shoppingBasketMapper.toDTO(found), SBOperation.UPDATED)
        return shoppingBasketMapper.toDTO(found)
    }

    fun modifyItemQuantity(shoppingBasketID: UUID, itemID: UUID, newQuantity: Int): ShoppingBasketDTO{
        if(newQuantity == 0){
            return removeItemFromBasket(shoppingBasketID, itemID)
        }
        val found = shoppingBasketRepository.findByIdOrNull(shoppingBasketID) ?: throw ShoppingBasketNotFoundException()
        found.updateItemQuantity(itemID, newQuantity)
        shoppingBasketRepository.save(found)
        producer.sendMessage(shoppingBasketMapper.toDTO(found), SBOperation.UPDATED)
        return shoppingBasketMapper.toDTO(found)
    }

    fun removeItemFromBasket(shoppingBasketID: UUID, itemID: UUID): ShoppingBasketDTO{
        val found = shoppingBasketRepository.findByIdOrNull(shoppingBasketID) ?: throw ShoppingBasketNotFoundException()
        found.removeItemFromBasket(itemID)
        shoppingBasketRepository.save(found)
        producer.sendMessage(shoppingBasketMapper.toDTO(found), SBOperation.UPDATED)
        return shoppingBasketMapper.toDTO(found)
    }

    fun proceedToCheckout(shoppingBasketID: UUID): ShoppingBasketDTO {
        val found = shoppingBasketRepository.findByIdOrNull(shoppingBasketID) ?: throw ShoppingBasketNotFoundException()
        if(found.readyToCheckout()) {
            producer.sendMessage(shoppingBasketMapper.toDTO(found), SBOperation.CHECKOUT)
            logger.info("Customer ${found.customerID} proceeded to checkout with shopping basket: $found")
            deleteShoppingBasket(shoppingBasketID)
        }
        else{
            logger.error("Customer ${found.customerID} tried to checkout with unavailable items in shopping basket: $found")
            throw CheckoutUnavailableException("Shopping basket contains unavailable items")
        }
        return shoppingBasketMapper.toDTO(found)
    }

    fun deleteShoppingBasket(shoppingBasketID: UUID): Boolean {
        val found = shoppingBasketRepository.findByIdOrNull(shoppingBasketID)
        if (found != null) {
            val dto = shoppingBasketMapper.toDTO(found)
            shoppingBasketRepository.deleteById(shoppingBasketID)
            producer.sendMessage(dto, SBOperation.DELETED)
            logger.info("Shopping basket $shoppingBasketID deleted")
            return true
        }
        return false
    }

    fun deleteAllShoppingBaskets() {
        shoppingBasketRepository.deleteAll()
    }

    fun getAllShoppingBaskets(): List<ShoppingBasketDTO>{
        return shoppingBasketRepository.findAll().map { shoppingBasketMapper.toDTO(it) }
    }

    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher
    }
}
