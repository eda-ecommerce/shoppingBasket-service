package eda.shoppingBasket.service

import eda.shoppingBasket.service.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.entity.ShoppingBasket
import eda.shoppingBasket.service.entity.ShoppingBasketItem
import eda.shoppingBasket.service.repository.OfferingRepository
import eda.shoppingBasket.service.repository.ShoppingBasketItemRepository
import eda.shoppingBasket.service.repository.ShoppingBasketRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShoppingBasketService(private val shoppingBasketRepository: ShoppingBasketRepository, private val shoppingBasketItemRepository: ShoppingBasketItemRepository, private val offeringRepository: OfferingRepository) {

    @Autowired
    lateinit var shoppingBasketMapper: ShoppingBasketMapper

    fun createShoppingBasket(shoppingBasketDTO: ShoppingBasketDTO): ShoppingBasketDTO {
        //check for existing shopping basket
        var found = shoppingBasketRepository.findByShoppingBasketID(shoppingBasketDTO.shoppingBasketID)
        if (found != null) { //TODO: figure out why isPresent is throwing an error on my machine. just using != null for now
            //maybe return something like:
            throw Exception("Shopping basket with id ${shoppingBasketDTO.shoppingBasketID} already exists.")
        }
        val newShoppingBasket = shoppingBasketMapper.toEntity(shoppingBasketDTO)
        shoppingBasketRepository.save(newShoppingBasket)
        return shoppingBasketMapper.toDTO(newShoppingBasket)
    }

    fun createShoppingBasketWithCustomerID(customerID: UUID): ShoppingBasketDTO {
        //TODO: check if a customer with this customerID exists
        //check for existing shopping basket with the same customerID
        var found = shoppingBasketRepository.findByCustomerID(customerID)
        if (found != null) { //TODO: figure out why isPresent is throwing an error on my machine. just using != null for now
            //maybe return something like:
            throw Exception("Shopping basket with customerID $customerID already exists.")
        }
        val newShoppingBasket = ShoppingBasket(customerID = customerID)
        shoppingBasketRepository.save(newShoppingBasket)
        return shoppingBasketMapper.toDTO(newShoppingBasket)
    }

    fun addOfferingToShoppingBasket(shoppingBasketID: UUID, offeringID: UUID, offeringAmount: Int): ShoppingBasketDTO? {
        val shoppingBasket = shoppingBasketRepository.findById(shoppingBasketID).get()
        if (shoppingBasket != null) {
            //get the offering from the offering repo, so that we can get the itemPrice
            val newOffering = offeringRepository.findById(offeringID).get()
            if (newOffering == null) return null //tried to use an Elvis operator in the previous line, but it's greyed out for some reason. feel free to add it back if you want to
            val newShoppingBasketItem = ShoppingBasketItem(shoppingBasket = shoppingBasket, quantity = offeringAmount, itemPrice = newOffering.unitPrice)
            newShoppingBasketItem.shoppingBasket = shoppingBasket
            shoppingBasketItemRepository.save(newShoppingBasketItem)
            //add the new offering to the shopping basket one to many list
            shoppingBasket.shoppingBasketItems.add(newShoppingBasketItem)
            //add the shopping basket to the shopping basket repo
            shoppingBasketRepository.save(shoppingBasket)
            return shoppingBasketMapper.toDTO(shoppingBasket)
        }
        return null
    }

    fun removeItemFromShoppingBasket(shoppingBasketID: UUID, shoppingBasketItemID: UUID): ShoppingBasketDTO? {
        val shoppingBasket = shoppingBasketRepository.findById(shoppingBasketID).get()
        if (shoppingBasket == null) {
            //maybe return something like:
            throw Exception("Shopping basket with id $shoppingBasketID does not exist.")
        }
        else if (shoppingBasket.shoppingBasketItems.isEmpty()) {
            //maybe return something like:
            throw Exception("Shopping basket with id $shoppingBasketID is empty.")
        }
        else if (shoppingBasket.shoppingBasketItems.none { it.shoppingBasketItemID == shoppingBasketItemID }) {
            //maybe return something like:
            throw Exception("Shopping basket with id $shoppingBasketID does not contain an item with id $shoppingBasketItemID.")
        }
        else {
            val shoppingBasketItem = shoppingBasket.shoppingBasketItems.find { it.shoppingBasketItemID == shoppingBasketItemID }
            shoppingBasket.shoppingBasketItems.remove(shoppingBasketItem)
            //i am also removing the shopping basket item from the shopping basket item repo here, not sure if this is what we need rn
            shoppingBasketItemRepository.deleteById(shoppingBasketItemID)
            shoppingBasketRepository.save(shoppingBasket)
            return shoppingBasketMapper.toDTO(shoppingBasket)
        }
        return null
    }

    fun modifyItemQuantity(shoppingBasketID: UUID, shoppingBasketItemID: UUID, newQuantity: Int): ShoppingBasketDTO {
        val shoppingBasket = shoppingBasketRepository.findById(shoppingBasketID).get()
        if (shoppingBasket == null) {
            //maybe return something like:
            throw Exception("Shopping basket with id $shoppingBasketID does not exist.")
        }
        else if (shoppingBasket.shoppingBasketItems.isEmpty()) {
            //maybe return something like:
            throw Exception("Shopping basket with id $shoppingBasketID is empty.")
        }
        else if (shoppingBasket.shoppingBasketItems.none { it.shoppingBasketItemID == shoppingBasketItemID }) {
            //maybe return something like:
            throw Exception("Shopping basket with id $shoppingBasketID does not contain an item with id $shoppingBasketItemID.")
        }
        else {
            val shoppingBasketItem = shoppingBasket.shoppingBasketItems.find { it.shoppingBasketItemID == shoppingBasketItemID }
            shoppingBasketItem!!.quantity = newQuantity
            shoppingBasketItemRepository.save(shoppingBasketItem)
            //updating the shopping basket item repo here too; not sure if this is what we need rn
            shoppingBasketRepository.save(shoppingBasket)
            return shoppingBasketMapper.toDTO(shoppingBasket)
        }
    }

    fun getShoppingBasket(shoppingBasketID: UUID): ShoppingBasket {
        return shoppingBasketRepository.findById(shoppingBasketID).get()
    }

    fun getShoppingBasketDTO(shoppingBasketID: UUID): ShoppingBasketDTO {
        val shoppingBasket = shoppingBasketRepository.findById(shoppingBasketID).get()
        return shoppingBasketMapper.toDTO(shoppingBasket)
    }

    fun getShoppingBasketByCustomerID(customerID: UUID): ShoppingBasket {
        return shoppingBasketRepository.findByCustomerID(customerID)
    }

    fun getShoppingBasketDTOByCustomerID(customerID: UUID): ShoppingBasketDTO {
        val shoppingBasket = shoppingBasketRepository.findByCustomerID(customerID)
        return shoppingBasketMapper.toDTO(shoppingBasket)
    }

    fun numberOfItemsInShoppingBasket(shoppingBasketID: UUID): Int =
        shoppingBasketRepository.findById(shoppingBasketID).get().numberOfItemsInShoppingBasket()

    fun getAllShoppingBaskets(): MutableIterable<ShoppingBasket> {
        return shoppingBasketRepository.findAll()
    }

    fun deleteShoppingBasket(shoppingBasketID: UUID) {
        shoppingBasketRepository.deleteById(shoppingBasketID)
    }

    fun deleteAllShoppingBaskets() {
        shoppingBasketRepository.deleteAll()
    }

    fun updateShoppingBasket(shoppingBasket: ShoppingBasket): ShoppingBasketDTO {
        shoppingBasketRepository.save(shoppingBasket)
        return shoppingBasketMapper.toDTO(shoppingBasket)
    }

//    fun saveShoppingBasket(shoppingBasket: ShoppingBasket): ShoppingBasketDTO {
//        //i think this is (/will be) similar to updateShoppingBasket()
//        //lmk if you think otherwise
//    }

}
