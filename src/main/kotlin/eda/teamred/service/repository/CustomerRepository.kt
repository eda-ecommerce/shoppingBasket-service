package eda.teamred.service.repository

import eda.teamred.service.entity.Customer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CustomerRepository: CrudRepository<Customer, UUID>{
}
