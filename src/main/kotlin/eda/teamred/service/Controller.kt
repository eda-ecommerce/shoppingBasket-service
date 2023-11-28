package eda.teamred.service

import eda.teamred.service.dto.CustomerDTO
import org.springframework.http.HttpStatus
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
    fun create(@RequestBody customerDTO : CustomerDTO){
        customerApplicationService.createCustomer(customerDTO)
    }

    @GetMapping("/customer")
    fun fetch() : List<CustomerDTO>{
        return customerApplicationService.fetchCustomers()
    }

    @PutMapping("/customer/{id}")
    fun update(@RequestBody customerDTO: CustomerDTO, @PathVariable("id") id : UUID) {
        customerApplicationService.updateCustomer(customerDTO, id)
    }

    @DeleteMapping("/customer/{id}")
    fun delete(@PathVariable("id") id : UUID){
        customerApplicationService.deleteCustomer(id)
    }

}
