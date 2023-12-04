package eda.teamred.service

import eda.teamred.service.dto.CustomerDTO
import eda.teamred.service.entity.Customer
import eda.teamred.service.repository.CustomerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.ui.ModelMap
import java.util.*

@Service
class CustomerApplicationService(private val customerRepository: CustomerRepository, private val producer: StringProducer) {
    fun sendTestMessage(message: String){
        producer.sendStringMessage(message)
    }

    fun createCustomer(customerDTO: CustomerDTO) : UUID{
        val newCustomer = Customer(customerDTO.firstName,customerDTO.lastName,customerDTO.address)
        customerRepository.save(newCustomer)
        producer.sendStringMessage("Created new Customer with id: ${newCustomer.id}")
        return newCustomer.id
    }

    fun fetchCustomers() : List<CustomerDTO>{
        val customers = customerRepository.findAll().toList()
        return customers.map { C : Customer -> CustomerDTO(C.firstName, C.lastName, C.address) }
    }

    fun fetchCustomerById(id : UUID) : CustomerDTO?{
        val customer = customerRepository.findByIdOrNull(id)
        var customerDTO : CustomerDTO? = null
        if(customer != null) customerDTO = CustomerDTO(customer.firstName, customer.lastName, customer.address)
        return customerDTO
    }

    fun updateCustomer(customerDTO: CustomerDTO, id : UUID){
        val customer = customerRepository.findById(id).get()
        customer.firstName = customerDTO.firstName
        customer.lastName = customerDTO.lastName
        customer.address = customerDTO.address
        customerRepository.save(customer)
        producer.sendStringMessage("Updated Customer with id: $id")
    }

    fun deleteCustomer(id: UUID){
        customerRepository.deleteById(id)
        producer.sendStringMessage("Deleted Customer with id: $id")
    }
}
