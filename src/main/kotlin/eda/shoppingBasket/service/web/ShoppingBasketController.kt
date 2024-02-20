package eda.shoppingBasket.service.web

import eda.shoppingBasket.service.application.ShoppingBasketService
import eda.shoppingBasket.service.model.dto.OfferingInBasketDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ShoppingBasketController (private val shoppingBasketService: ShoppingBasketService) {
    @Operation(summary = "Create a new shopping basket")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Shopping basket created"),
        ApiResponse(responseCode = "417", description = "Shopping basket already exists"),
        ApiResponse(responseCode = "400", description = "Invalid offering or otherwise bad request")
    ])
    @PostMapping("/shoppingBasket")
    fun createShoppingBasket(@RequestBody shoppingBasketDTO: ShoppingBasketDTO): ResponseEntity<out Any> {
        return ResponseEntity(shoppingBasketService.createShoppingBasket(shoppingBasketDTO), HttpStatus.CREATED)
    }

    //adding an offering to a shopping basket
    @Operation(summary = "Add an offering to a shopping basket")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Offering added to shopping basket"),
        ApiResponse(responseCode = "404", description = "Shopping basket not found"),
        ApiResponse(responseCode = "400", description = "Invalid offering or otherwise bad request")
    ])
    @PostMapping("/shoppingBasket/{shoppingBasketID}/addOffering") // POST /shoppingBasket/81765f-128512-f1238/81765f-128512-f1238/2
    fun addOfferingToShoppingBasket(@RequestBody offeringInBasketDTO: OfferingInBasketDTO, @PathVariable shoppingBasketID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val savedShoppingBasketDTO = shoppingBasketService.addOfferingToShoppingBasket(shoppingBasketID, offeringInBasketDTO.offeringID, offeringInBasketDTO.quantity)
        return ResponseEntity(savedShoppingBasketDTO, HttpStatus.CREATED)
    }

    //getting the shopping basket of a customer
    @Operation(summary = "Get a shopping basket by customer ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Shopping basket found"),
        ApiResponse(responseCode = "404", description = "Shopping basket not found")
    ])
    @GetMapping("/shoppingBasket") //GET /shoppingBasket?customerID=81765f-128512-f1238
    fun getShoppingBasketByCustomerID(@RequestParam customerID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val shoppingBasketDTO = shoppingBasketService.getShoppingBasketDTOByCustomerID(customerID)
        return ResponseEntity(shoppingBasketDTO, HttpStatus.OK)
    }
    @Operation(summary = "Get all shopping baskets")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Returns all found shopping baskets"),
    ])
    @GetMapping("/shoppingBaskets")
    fun getAllShoppingBaskets(): ResponseEntity<List<ShoppingBasketDTO>> {
        val shoppingBaskets = shoppingBasketService.getAllShoppingBaskets()
        return ResponseEntity(shoppingBaskets, HttpStatus.OK)
    }

    @Operation(summary = "Get a shopping basket by its ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Shopping basket found"),
        ApiResponse(responseCode = "404", description = "Shopping basket not found")
    ])
    @GetMapping("/shoppingBasket/{shoppingBasketID}")
    fun getShoppingBasketByID(@PathVariable shoppingBasketID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val shoppingBasketDTO = shoppingBasketService.getShoppingBasketDTO(shoppingBasketID)
        return ResponseEntity(shoppingBasketDTO, HttpStatus.OK)
    }

    @Operation(summary = "Change the quantity of an offering in a shopping basket")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Offering quantity modified"),
        ApiResponse(responseCode = "404", description = "Shopping basket / item not found"),
        ApiResponse(responseCode = "400", description = "Invalid offering or otherwise bad request")
    ])
    @PostMapping("/shoppingBasket/{shoppingBasketID}/items/{itemID}/changeQuantity")
    fun modifyOfferingQuantity(@PathVariable shoppingBasketID: UUID, @PathVariable itemID: UUID, @RequestBody offeringInBasketDTO: OfferingInBasketDTO): ResponseEntity<ShoppingBasketDTO> {
        val modifiedShoppingBasketDTO = shoppingBasketService.modifyItemQuantity(shoppingBasketID, itemID, offeringInBasketDTO.quantity)
        return ResponseEntity(modifiedShoppingBasketDTO, HttpStatus.OK)
    }

    @Operation(summary = "Fully remove an offering from a shopping basket")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Offering removed from shopping basket"),
        ApiResponse(responseCode = "404", description = "Shopping basket / item not found")
    ])
    @DeleteMapping("/shoppingBasket/{shoppingBasketID}/items/{itemID}")
    fun removeOfferingFromShoppingBasket(@PathVariable shoppingBasketID: UUID, @PathVariable itemID : UUID): ResponseEntity<ShoppingBasketDTO> {
        val modifiedShoppingBasketDTO = shoppingBasketService.removeItemFromShoppingBasket(shoppingBasketID, itemID)
        return ResponseEntity(modifiedShoppingBasketDTO, HttpStatus.OK)
    }

    @Operation(summary = "Delete a shopping basket")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Shopping basket deleted"),
        ApiResponse(responseCode = "404", description = "Shopping basket not found")
    ])
    @DeleteMapping("/shoppingBasket/{shoppingBasketID}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteShoppingBasket(@PathVariable shoppingBasketID: UUID) {
        shoppingBasketService.deleteShoppingBasket(shoppingBasketID)
    }

    @Operation(summary = "Checkout a shopping basket, also deletes the shopping basket")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Shopping basket checked out"),
        ApiResponse(responseCode = "404", description = "Shopping basket not found")
    ])
    @DeleteMapping("/shoppingBasket/{shoppingBasketID}/checkout")
    fun checkoutShoppingBasket(@PathVariable shoppingBasketID: UUID): ResponseEntity<ShoppingBasketDTO> {
        val dto = shoppingBasketService.proceedToCheckout(shoppingBasketID)
        return ResponseEntity(dto, HttpStatus.OK)
    }
}


