package eda.shoppingBasket.service.application

import eda.shoppingBasket.service.eventing.offering.OfferingAvailableEvent
import eda.shoppingBasket.service.eventing.offering.OfferingUnavailableEvent
import eda.shoppingBasket.service.model.entity.Offering
import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import eda.shoppingBasket.service.repository.ShoppingBasketItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener

@Service
class ShoppingBasketItemService {

    @Autowired
    private lateinit var shoppingBasketItemRepository: ShoppingBasketItemRepository

    fun createShoppingBasketItem(offering: Offering, count: Int): ShoppingBasketItem{
        return shoppingBasketItemRepository.save(ShoppingBasketItem(
            quantity = count,
            subtotal = offering.price * count,
            offeringID = offering.id,
            offeringPrice = offering.price
        ))
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    fun handleOfferingUnavailableEvent(event: OfferingUnavailableEvent){
        val shoppingBasketItems = shoppingBasketItemRepository.findAllByOfferingID(event.offeringId)
        shoppingBasketItems.forEach {
            it.disableItem()
            shoppingBasketItemRepository.save(it)}
    }
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    fun handleOfferingAvailableEvent(event: OfferingAvailableEvent){
        val shoppingBasketItems = shoppingBasketItemRepository.findAllByOfferingID(event.offeringId)
        shoppingBasketItems.forEach {
            it.enableItem()
            shoppingBasketItemRepository.save(it)}
    }
}