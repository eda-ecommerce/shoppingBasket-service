package eda.teamred.repository

import eda.teamred.dto.CustomerDTO
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CustomerRepository : CrudRepository<CustomerDTO, UUID> {
}