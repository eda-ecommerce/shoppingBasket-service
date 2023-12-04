package eda.teamred.service

import eda.teamred.service.dto.CustomerDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class Controller (private val customerApplicationService: CustomerApplicationService) {
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun sendMessage(
        @RequestBody requestBody: StringMessage
    ){
        customerApplicationService.sendTestMessage(requestBody.message)
    }
    data class StringMessage(val message: String)

    @PostMapping("/customer")
    fun create(@RequestBody customerDTO : CustomerDTO) : ResponseEntity<UUID>{
        return ResponseEntity(customerApplicationService.createCustomer(customerDTO), HttpStatus.CREATED)
    }

    @GetMapping("/customer")
    fun fetch() : ResponseEntity<List<CustomerDTO>>{
        return ResponseEntity(customerApplicationService.fetchCustomers(), HttpStatus.OK)
    }

    @GetMapping("/customer/{id}")
    fun fetchById(@PathVariable("id") id : UUID) : ResponseEntity<CustomerDTO>{
        val customerDto = customerApplicationService.fetchCustomerById(id)
        return if (customerDto == null) ResponseEntity(null, HttpStatus.NOT_FOUND) else ResponseEntity(customerDto, HttpStatus.OK)
    }

    @PutMapping("/customer/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(@RequestBody customerDTO: CustomerDTO, @PathVariable("id") id : UUID){
        customerApplicationService.updateCustomer(customerDTO, id)
    }

    @DeleteMapping("/customer/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun delete(@PathVariable("id") id : UUID){
        customerApplicationService.deleteCustomer(id)
    }

}
