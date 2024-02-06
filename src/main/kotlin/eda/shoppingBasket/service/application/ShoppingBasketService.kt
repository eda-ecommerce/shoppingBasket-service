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
        if (shoppingBasketDTO.shoppingBasketID != null) {
            val found = shoppingBasketRepository.findByShoppingBasketID(shoppingBasketDTO.shoppingBasketID!!)
            if (found != null) {
                throw ShoppingBasketDuplicationException("Shopping basket with id ${shoppingBasketDTO.shoppingBasketID} already exists.")
            }
        }
        val newShoppingBasket = shoppingBasketMapper.toEntity(shoppingBasketDTO)
        val items = mutableListOf<ShoppingBasketItemDTO>()
        shoppingBasketRepository.save(newShoppingBasket)
        if (shoppingBasketDTO.shoppingBasketItems.isNotEmpty()){
            shoppingBasketDTO.shoppingBasketItems.forEach {
                items.add(itemService.addOfferingToShoppingBasket(newShoppingBasket, it.offeringID, it.quantity))
            }
        }
        val dto = shoppingBasketMapper.toDTO(newShoppingBasket, items)
        producer.sendMessage(dto, SBOperation.CREATE)
        return dto
    }

    fun createShoppingBasketWithCustomerID(customerID: UUID): ShoppingBasketDTO {
        //check for existing shopping basket with the same customerID
        val found = shoppingBasketRepository.findByCustomerID(customerID)
        if (found != null) {
            throw ShoppingBasketDuplicationException("Shopping basket for customerID $customerID already exists.")
        }
        val newShoppingBasket = ShoppingBasket(customerID = customerID)
        shoppingBasketRepository.save(newShoppingBasket)
        logger.info("Created SB for customerID $customerID")
        return shoppingBasketMapper.toDTO(newShoppingBasket, mutableListOf())
    }

    fun addOfferingToShoppingBasket(shoppingBasketID: UUID, offeringID: UUID, offeringAmount: Int): ShoppingBasketDTO? {
        val shoppingBasket = getShoppingBasket(shoppingBasketID)
        itemService.addOfferingToShoppingBasket(shoppingBasket, offeringID, offeringAmount)
        val items = itemService.getItemsInShoppingBasket(shoppingBasket)
        val dto = shoppingBasketMapper.toDTO(shoppingBasket, items)
        producer.sendMessage(dto, SBOperation.UPDATE)
        return dto
    }

    fun removeItemFromShoppingBasket(shoppingBasketID: UUID, shoppingBasketItemID: UUID): ShoppingBasketDTO? {
        val shoppingBasket = getShoppingBasket(shoppingBasketID)
        itemService.removeOfferingFromShoppingBasket(shoppingBasket, shoppingBasketItemID)
        val items = itemService.getItemsInShoppingBasket(shoppingBasket)
        return shoppingBasketMapper.toDTO(shoppingBasket, items)
    }

    fun modifyItemQuantity(shoppingBasketID: UUID, shoppingBasketItemID: UUID, newQuantity: Int): ShoppingBasketDTO {
        val shoppingBasket = getShoppingBasket(shoppingBasketID)
        itemService.changeQuantity(shoppingBasket, shoppingBasketItemID, newQuantity)
        val items = itemService.getItemsInShoppingBasket(shoppingBasket)
        return shoppingBasketMapper.toDTO(shoppingBasket, items)
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

    fun getAllShoppingBaskets(): MutableIterable<ShoppingBasket> {
        return shoppingBasketRepository.findAll()
    }

    fun deleteShoppingBasket(shoppingBasketID: UUID): Boolean {
        val found = shoppingBasketRepository.findByShoppingBasketID(shoppingBasketID)
        if (found != null) {
            shoppingBasketRepository.deleteById(shoppingBasketID)
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
