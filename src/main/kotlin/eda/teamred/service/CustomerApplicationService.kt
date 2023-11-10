package eda.teamred.service

import eda.teamred.service.dto.CustomerDTO
import eda.teamred.service.entity.Customer
import eda.teamred.service.repository.CustomerRepository
import org.springframework.stereotype.Service

@Service
class CustomerApplicationService(private val customerRepository: CustomerRepository, private val producer: StringProducer) {
    fun sendTestMessage(message: String){
        producer.sendStringMessage(message)
    }

    fun createCustomer(customerDTO: CustomerDTO){
        val newCustomer = Customer(customerDTO.firstName,customerDTO.lastName,customerDTO.address)
        customerRepository.save(newCustomer)
        producer.sendStringMessage("Created new Customer with id: ${newCustomer.id}")
    }
}
