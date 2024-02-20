package eda.shoppingBasket.service.application

import eda.shoppingBasket.service.application.exception.ShoppingBasketDuplicationException
import eda.shoppingBasket.service.application.exception.ShoppingBasketNotFoundException
import eda.shoppingBasket.service.eventing.SBOperation
import eda.shoppingBasket.service.eventing.ShoppingBasketProducer
import eda.shoppingBasket.service.model.ShoppingBasketMapper
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketItemDTO
import eda.shoppingBasket.service.model.entity.ShoppingBasket
import eda.shoppingBasket.service.repository.ShoppingBasketRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShoppingBasketService(private val shoppingBasketRepository: ShoppingBasketRepository) {

    @Autowired
    lateinit var shoppingBasketMapper: ShoppingBasketMapper

    @Autowired
    lateinit var itemService: ShoppingBasketItemService

    @Autowired
    private lateinit var producer: ShoppingBasketProducer

    final val logger = LoggerFactory.getLogger(ShoppingBasketService::class.java)

    fun createShoppingBasket(shoppingBasketDTO: ShoppingBasketDTO): ShoppingBasketDTO {
        //check for existing shopping basket
        if (shoppingBasketDTO.shoppingBasketId != null) {
            val found = shoppingBasketRepository.findByShoppingBasketID(shoppingBasketDTO.shoppingBasketId!!)
            if (found != null) {
                throw ShoppingBasketDuplicationException("Shopping basket with id ${shoppingBasketDTO.shoppingBasketId} already exists.")
            }
        }
        if(shoppingBasketDTO.customerId != null) {
            val found = shoppingBasketRepository.findByCustomerID(shoppingBasketDTO.customerId!!)
            if (found != null) {
                throw ShoppingBasketDuplicationException("Shopping basket for customerID ${shoppingBasketDTO.customerId} already exists.")
            }
        }
        val newShoppingBasket = shoppingBasketMapper.toEntity(shoppingBasketDTO)
        val items = mutableListOf<ShoppingBasketItemDTO>()
        shoppingBasketRepository.save(newShoppingBasket)
        if (shoppingBasketDTO.items.isNotEmpty()){
            shoppingBasketDTO.items.forEach {
                items.add(itemService.addOfferingToShoppingBasket(newShoppingBasket, it.offeringId, it.quantity))
            }
        }
        val dto = shoppingBasketMapper.toDTO(newShoppingBasket, items)
        producer.sendMessage(dto, SBOperation.CREATED)
        logger.info("Shopping basket created with id: ${dto.shoppingBasketId}")
        return dto
    }

    fun addOfferingToShoppingBasket(shoppingBasketID: UUID, offeringID: UUID, offeringAmount: Int): ShoppingBasketDTO? {
        val shoppingBasket = getShoppingBasket(shoppingBasketID)
        itemService.addOfferingToShoppingBasket(shoppingBasket, offeringID, offeringAmount)
        val items = itemService.getItemsInShoppingBasket(shoppingBasket)
        val dto = shoppingBasketMapper.toDTO(shoppingBasket, items)
        producer.sendMessage(dto, SBOperation.UPDATED)
        logger.info("Offering $offeringID added to shopping basket $shoppingBasketID")
        return dto
    }

    fun removeItemFromShoppingBasket(shoppingBasketID: UUID, shoppingBasketItemID: UUID): ShoppingBasketDTO? {
        val shoppingBasket = getShoppingBasket(shoppingBasketID)
        val removed = itemService.removeOfferingFromShoppingBasket(shoppingBasket, shoppingBasketItemID)
        val items = itemService.getItemsInShoppingBasket(shoppingBasket)
        val dto = shoppingBasketMapper.toDTO(shoppingBasket, items)
        producer.sendMessage(dto, SBOperation.UPDATED)
        logger.info("Offering ${removed?.offeringId} removed from shopping basket $shoppingBasketID")
        return dto
    }

    fun modifyItemQuantity(shoppingBasketID: UUID, shoppingBasketItemID: UUID, newQuantity: Int): ShoppingBasketDTO {
        val shoppingBasket = getShoppingBasket(shoppingBasketID)
        val modified = itemService.changeQuantity(shoppingBasket, shoppingBasketItemID, newQuantity)
        val items = itemService.getItemsInShoppingBasket(shoppingBasket)
        val dto = shoppingBasketMapper.toDTO(shoppingBasket, items)
        producer.sendMessage(dto, SBOperation.UPDATED)
        logger.info("Offering ${modified?.offeringId} quantity modified in shopping basket $shoppingBasketID")
        return dto
    }

    fun getShoppingBasket(shoppingBasketID: UUID): ShoppingBasket {
        return shoppingBasketRepository.findByIdOrNull(shoppingBasketID)?: throw ShoppingBasketNotFoundException()
    }

    fun getShoppingBasketDTO(shoppingBasketID: UUID): ShoppingBasketDTO {
        val shoppingBasket = getShoppingBasket(shoppingBasketID)
        val items = itemService.getItemsInShoppingBasket(shoppingBasket)
        return shoppingBasketMapper.toDTO(shoppingBasket, items)
    }

    fun getShoppingBasketByCustomerID(customerID: UUID): ShoppingBasket {
        return shoppingBasketRepository.findByCustomerID(customerID)?: throw ShoppingBasketNotFoundException()
    }

    fun getShoppingBasketDTOByCustomerID(customerID: UUID): ShoppingBasketDTO {
        val shoppingBasket = shoppingBasketRepository.findByCustomerID(customerID)?: throw ShoppingBasketNotFoundException()
        return shoppingBasketMapper.toDTO(shoppingBasket, itemService.getItemsInShoppingBasket(shoppingBasket))
    }

    fun numberOfItemsInShoppingBasket(shoppingBasketID: UUID): Int =
        itemService.getNumberOfItemsInShoppingBasket(getShoppingBasket(shoppingBasketID))

    fun getAllShoppingBaskets(): List<ShoppingBasketDTO> {
        return shoppingBasketRepository.findAll().map { shoppingBasket ->
            shoppingBasketMapper.toDTO(shoppingBasket, itemService.getItemsInShoppingBasket(shoppingBasket))
        }
    }

    fun proceedToCheckout(shoppingBasketID: UUID): ShoppingBasketDTO {
        val sbDto = getShoppingBasketDTO(shoppingBasketID)
        producer.sendMessage(sbDto, SBOperation.CHECKOUT)
        logger.info("Customer ${sbDto.customerId} proceeded to checkout with shopping basket: $sbDto")
        deleteShoppingBasket(shoppingBasketID)
        return sbDto
    }

    fun deleteShoppingBasket(shoppingBasketID: UUID): Boolean {
        val found = shoppingBasketRepository.findByShoppingBasketID(shoppingBasketID)
        if (found != null) {
            val dto = getShoppingBasketDTO(shoppingBasketID)
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

    fun updateShoppingBasket(shoppingBasket: ShoppingBasket): ShoppingBasketDTO {
        shoppingBasketRepository.save(shoppingBasket)
        return shoppingBasketMapper.toDTO(shoppingBasket, itemService.getItemsInShoppingBasket(shoppingBasket))
    }

}
