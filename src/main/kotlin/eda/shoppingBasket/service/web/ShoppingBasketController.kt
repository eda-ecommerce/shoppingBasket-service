package eda.shoppingBasket.service.web

import eda.shoppingBasket.service.application.ShoppingBasketService
import eda.shoppingBasket.service.model.dto.OfferingInBasketDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ShoppingBasketController (private val shoppingBasketService: ShoppingBasketService) {
    @Operation(summary = "Create a new shopping basket")
    //creating a new shopping basket
    @PostMapping("/shoppingBasket")
    fun createShoppingBasket(@RequestBody shoppingBasketDTO: ShoppingBasketDTO): ResponseEntity<out Any> {
        return ResponseEntity(shoppingBasketService.createShoppingBasket(shoppingBasketDTO), HttpStatus.CREATED)
    }

    //creating a new shopping basket with a customerID
    @PostMapping("/shoppingBasket/customer/{customerID}") //POST /shoppingBasket/81765f-128512-f1238
    fun createShoppingBasketWithCustomerID(@PathVariable customerID: UUID, @RequestBody shoppingBasketDTO: ShoppingBasketDTO) : ResponseEntity<ShoppingBasketDTO> {
        return ResponseEntity(shoppingBasketService.createShoppingBasketWithCustomerID(customerID), HttpStatus.CREATED)
    }

    //adding an offering to a shopping basket
    @Operation(summary = "Add an offering to a shopping basket")
    @PostMapping("/shoppingBasket/{shoppingBasketID}/addOffering") // POST /shoppingBasket/81765f-128512-f1238/81765f-128512-f1238/2
    fun addOfferingToShoppingBasket(@RequestBody offeringInBasketDTO: OfferingInBasketDTO, @PathVariable shoppingBasketID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val savedShoppingBasketDTO = shoppingBasketService.addOfferingToShoppingBasket(shoppingBasketID, offeringInBasketDTO.offeringID, offeringInBasketDTO.quantity)
        return ResponseEntity(savedShoppingBasketDTO, HttpStatus.CREATED)
    }

    //getting the shopping basket of a customer
    @GetMapping("/shoppingBasket") //GET /shoppingBasket?customerID=81765f-128512-f1238
    fun getShoppingBasketByCustomerID(@RequestParam customerID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val shoppingBasketDTO = shoppingBasketService.getShoppingBasketDTOByCustomerID(customerID)
        return ResponseEntity(shoppingBasketDTO, HttpStatus.OK)
    }
    @GetMapping("/shoppingBaskets")
    fun getAllShoppingBaskets(): ResponseEntity<List<ShoppingBasketDTO>> {
        val shoppingBaskets = shoppingBasketService.getAllShoppingBaskets()
        return ResponseEntity(shoppingBaskets, HttpStatus.OK)
    }

    //getting a shopping basket by its ID
    @GetMapping("/shoppingBasket/{shoppingBasketID}")
    fun getShoppingBasketByID(@PathVariable shoppingBasketID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val shoppingBasketDTO = shoppingBasketService.getShoppingBasketDTO(shoppingBasketID)
        return ResponseEntity(shoppingBasketDTO, HttpStatus.OK)
    }

    //modifying the quantity of an offering in a shopping basket
    @PostMapping("/shoppingBasket/{shoppingBasketID}/items/{itemID}/changeQuantity")
    fun modifyOfferingQuantity(@PathVariable shoppingBasketID: UUID, @PathVariable itemID: UUID, @RequestBody offeringInBasketDTO: OfferingInBasketDTO): ResponseEntity<ShoppingBasketDTO> {
        val modifiedShoppingBasketDTO = shoppingBasketService.modifyItemQuantity(shoppingBasketID, itemID, offeringInBasketDTO.quantity)
        return ResponseEntity(modifiedShoppingBasketDTO, HttpStatus.OK)
    }


    //removing an offering from a shopping basket
    @DeleteMapping("/shoppingBasket/{shoppingBasketID}/items/{itemID}")
    fun removeOfferingFromShoppingBasket(@PathVariable shoppingBasketID: UUID, @PathVariable itemID : UUID): ResponseEntity<ShoppingBasketDTO> {
        val modifiedShoppingBasketDTO = shoppingBasketService.removeItemFromShoppingBasket(shoppingBasketID, itemID)
        return ResponseEntity(modifiedShoppingBasketDTO, HttpStatus.OK)
    }

    //delete a shopping basket
    @Operation(summary = "Delete a shopping basket")
    @DeleteMapping("/shoppingBasket/{shoppingBasketID}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteShoppingBasket(@PathVariable shoppingBasketID: UUID) {
        shoppingBasketService.deleteShoppingBasket(shoppingBasketID)
    }

    @Operation(summary = "Checkout a shopping basket, also deletes the shopping basket")
    @DeleteMapping("/shoppingBasket/{shoppingBasketID}/checkout")
    fun checkoutShoppingBasket(@PathVariable shoppingBasketID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val dto = shoppingBasketService.proceedToCheckout(shoppingBasketID)
        return ResponseEntity(dto, HttpStatus.OK)
    }
}


