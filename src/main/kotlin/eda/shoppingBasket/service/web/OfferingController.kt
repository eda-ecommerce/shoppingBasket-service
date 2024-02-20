package eda.shoppingBasket.service.web

import eda.shoppingBasket.service.application.OfferingService
import eda.shoppingBasket.service.model.dto.OfferingDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OfferingController {
    @Autowired
    lateinit var offeringService: OfferingService

    @Operation(summary = "Create a dummy offering")
    @PostMapping("/offering")
    fun createOffering(@RequestBody offeringDTO: OfferingDTO): ResponseEntity<OfferingDTO>{
        return ResponseEntity(offeringService.saveOffering(offeringDTO),HttpStatus.CREATED)
    }

    @Operation(summary = "Disable a dummy offering")
    @DeleteMapping("/offering/{offeringID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun disableOffering(@PathVariable offeringID: UUID){
        offeringService.disableOffering(offeringID)
    }

    @Operation(summary = "Get all offerings")
    @GetMapping("/offerings")
    fun getAllOfferings(): ResponseEntity<List<OfferingDTO>>{
        return ResponseEntity(offeringService.getAllOfferings(), HttpStatus.OK)
    }
}