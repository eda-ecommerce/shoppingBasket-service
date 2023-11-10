package eda.teamred.service.repository

import eda.teamred.service.entity.Customer
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface CustomerRepository: CrudRepository<Customer, UUID>{
}
