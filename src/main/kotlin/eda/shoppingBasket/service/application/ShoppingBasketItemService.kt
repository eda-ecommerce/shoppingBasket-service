package eda.shoppingBasket.service.application

import eda.shoppingBasket.service.application.exception.ShoppingBasketItemNotFoundException
import eda.shoppingBasket.service.eventing.OfferingUnavailableEvent
import eda.shoppingBasket.service.model.ShoppingBasketItemMapper
import eda.shoppingBasket.service.model.dto.ShoppingBasketItemDTO
import eda.shoppingBasket.service.model.entity.ItemState
import eda.shoppingBasket.service.model.entity.ShoppingBasket
import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import eda.shoppingBasket.service.repository.ShoppingBasketItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener
import java.util.*

@Service
class ShoppingBasketItemService {

    @Autowired
    private lateinit var shoppingBasketItemRepository: ShoppingBasketItemRepository

    @Autowired
    private lateinit var offeringService: OfferingService

    private val shoppingBasketItemMapper = ShoppingBasketItemMapper()

    fun addOfferingToShoppingBasket(shoppingBasket: ShoppingBasket, offeringID: UUID, offeringAmount: Int): ShoppingBasketItemDTO {
        val offering = offeringService.getOffering(offeringID)
        val totalPrice = offering.price * offeringAmount
        val shoppingBasketItem = ShoppingBasketItem(quantity = offeringAmount, shoppingBasket = shoppingBasket, offeringID = offeringID, totalPrice = totalPrice)
        shoppingBasketItemRepository.save(shoppingBasketItem)
        return shoppingBasketItemMapper.toDto(shoppingBasketItem)
    }

    fun removeOfferingFromShoppingBasket(shoppingBasket: ShoppingBasket, itemId: UUID): ShoppingBasketItemDTO?{
        val shoppingBasketItem = shoppingBasketItemRepository.findByShoppingBasketAndShoppingBasketItemID(shoppingBasket, itemId) ?: throw ShoppingBasketItemNotFoundException()
        shoppingBasketItemRepository.delete(shoppingBasketItem)
        return shoppingBasketItemMapper.toDto(shoppingBasketItem)
    }

    fun changeQuantity(shoppingBasket: ShoppingBasket, itemId: UUID, newQuantity: Int): ShoppingBasketItemDTO?{
        if (newQuantity <= 0) return removeOfferingFromShoppingBasket(shoppingBasket, itemId)
        val shoppingBasketItem = shoppingBasketItemRepository.findByShoppingBasketAndShoppingBasketItemID(shoppingBasket, itemId) ?: throw ShoppingBasketItemNotFoundException()
        shoppingBasketItem.totalPrice /= shoppingBasketItem.quantity
        shoppingBasketItem.quantity = newQuantity
        shoppingBasketItemRepository.save(shoppingBasketItem)
        return shoppingBasketItemMapper.toDto(shoppingBasketItem)
    }

    fun getSubtotal(shoppingBasketItem: ShoppingBasketItem): Float {
        val offering = offeringService.getOffering(shoppingBasketItem.offeringID)
        return offering.price * shoppingBasketItem.quantity
    }

    fun getNumberOfItemsInShoppingBasket(shoppingBasket: ShoppingBasket): Int {
        return shoppingBasketItemRepository.findByShoppingBasket(shoppingBasket).size
    }

    fun getItemsInShoppingBasket(shoppingBasket: ShoppingBasket): List<ShoppingBasketItemDTO> {
        val found = shoppingBasketItemRepository.findByShoppingBasket(shoppingBasket)
        return found.map { shoppingBasketItemMapper.toDto(it) }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    fun handleOfferingUnavailableEvent(event: OfferingUnavailableEvent){
        val shoppingBasketItems = shoppingBasketItemRepository.findAllById(listOf(event.offeringId))
        shoppingBasketItems.forEach {
            it.state = ItemState.UNAVAILABLE
            shoppingBasketItemRepository.save(it)}
    }


}