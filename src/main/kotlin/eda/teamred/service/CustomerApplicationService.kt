package eda.teamred.service

import eda.teamred.service.dto.CustomerDTO
import eda.teamred.service.entity.Customer
import eda.teamred.service.repository.CustomerRepository
import org.springframework.stereotype.Service
import java.util.*

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

    fun fetchCustomers() : List<CustomerDTO>{
        val customers = customerRepository.findAll().toList()
        return customers.map { C : Customer -> CustomerDTO(C.firstName, C.lastName, C.address) }
    }

    fun updateCustomer(customerDTO: CustomerDTO, id : UUID){
        val updatedCustomer = Customer(customerDTO.firstName,customerDTO.lastName,customerDTO.address, id)
        customerRepository.deleteById(id)
        customerRepository.save(updatedCustomer)
        producer.sendStringMessage("Updated Customer with id: ${updatedCustomer.id}")
    }

    fun deleteCustomer(id: UUID){
        customerRepository.deleteById(id)
        producer.sendStringMessage("Deleted Customer with id: $id")
    }
}
