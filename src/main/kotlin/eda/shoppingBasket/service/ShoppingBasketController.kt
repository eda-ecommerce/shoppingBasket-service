package eda.shoppingBasket.service

import eda.shoppingBasket.service.dto.ShoppingBasketDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*

@RestController
class ShoppingBasketController (private val shoppingBasketService: ShoppingBasketService) {

//    @ControllerAdvice
//    public class RestExceptionHandler: ResponseEntityExceptionHandler() {
//        @ExceptionHandler(value = [IllegalArgumentException::class])
//        fun handleConflict(ex: IllegalArgumentException): ResponseEntity<String> {
//            return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
//        }
//
//    }

    //creating a new shopping basket
    @PostMapping("/shoppingBasket")
    fun createShoppingBasket(@RequestBody shoppingBasketDTO: ShoppingBasketDTO): ResponseEntity<out Any> {
        return try {
            ResponseEntity(shoppingBasketService.createShoppingBasket(shoppingBasketDTO), HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
    }

    //creating a new shopping basket with a customerID
    @PostMapping("/shoppingBasket/{customerID}")
    fun createShoppingBasketWithCustomerID(@PathVariable customerID: UUID) : ResponseEntity<ShoppingBasketDTO> {
        //TODO: we are not checking for the validity of the customer ID here, should we do that?
        return ResponseEntity(shoppingBasketService.createShoppingBasketWithCustomerID(customerID), HttpStatus.CREATED)
    }

    //adding an offering to a shopping basket
    @PostMapping("/shoppingBasket/{shoppingBasketID}/{offeringID}/{offeringAmount}")
    fun addOfferingToShoppingBasket(@PathVariable offeringAmount: Int, @PathVariable offeringID: UUID, @PathVariable shoppingBasketID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val shoppingBasket = shoppingBasketService.getShoppingBasket(shoppingBasketID)
        if (shoppingBasket != null) {
            val savedShoppingBasketDTO = shoppingBasketService.addOfferingToShoppingBasket(shoppingBasketID, offeringID, offeringAmount)
            if (savedShoppingBasketDTO != null) {
                return ResponseEntity(savedShoppingBasketDTO, HttpStatus.OK)
            }
        }
        return ResponseEntity(null, HttpStatus.NOT_FOUND)
    }


    //getting the shopping basket of a customer
    @GetMapping("/shoppingBasket/{customerID}")
    fun getShoppingBasketByCustomerID(@PathVariable customerID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val shoppingBasketDTO = shoppingBasketService.getShoppingBasketDTOByCustomerID(customerID)
        if (shoppingBasketDTO != null)
            return ResponseEntity(shoppingBasketDTO, HttpStatus.OK)
        return ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    //getting a shopping basket by its ID
    @GetMapping("/shoppingBasket/{shoppingBasketID}")
    fun getShoppingBasketByID(@PathVariable shoppingBasketID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val shoppingBasketDTO = shoppingBasketService.getShoppingBasketDTO(shoppingBasketID)
        if (shoppingBasketDTO != null)
            return ResponseEntity(shoppingBasketDTO, HttpStatus.OK)
        return ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    //modifying the quantity of an offering in a shopping basket
    @PostMapping("/shoppingBasket/{shoppingBasketID}/{shoppingBasketItemID}/{newQuantity}")
    fun modifyOfferingQuantity(@PathVariable shoppingBasketID: UUID, @PathVariable shoppingBasketItemID: UUID, @PathVariable newQuantity: Int): ResponseEntity<ShoppingBasketDTO> {
        val shoppingBasket = shoppingBasketService.getShoppingBasket(shoppingBasketID)
        if (shoppingBasket != null) {
            val modifiedShoppingBasketDTO = shoppingBasketService.modifyItemQuantity(shoppingBasketID, shoppingBasketItemID, newQuantity)
            if (modifiedShoppingBasketDTO != null) {
                return ResponseEntity(modifiedShoppingBasketDTO, HttpStatus.OK)
            }
        }
        return ResponseEntity(null, HttpStatus.NOT_FOUND)
    }


    //removing an offering from a shopping basket
    @PostMapping("/shoppingBasket/{shoppingBasketID}/{shoppingBasketItemID}")
    fun removeOfferingFromShoppingBasket(@PathVariable shoppingBasketID: UUID, @PathVariable shoppingBasketItemID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val shoppingBasket = shoppingBasketService.getShoppingBasket(shoppingBasketID)
        if (shoppingBasket != null) {
            val modifiedShoppingBasketDTO = shoppingBasketService.removeItemFromShoppingBasket(shoppingBasketID, shoppingBasketItemID)
            if (modifiedShoppingBasketDTO != null) {
                return ResponseEntity(modifiedShoppingBasketDTO, HttpStatus.OK)
            }
        }
        return ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    //delete a shopping basket
    @DeleteMapping("/shoppingBasket/{shoppingBasketID}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteShoppingBasket(@PathVariable shoppingBasketID: UUID) {
        shoppingBasketService.deleteShoppingBasket(shoppingBasketID)
    }


}


