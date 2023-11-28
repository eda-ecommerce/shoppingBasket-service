package eda.teamred.service

import eda.teamred.service.entity.Customer
import eda.teamred.service.repository.CustomerRepository
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension

//Needs a MYSQL Container Running as configured in application.properties
@DataJpaTest
//Annotate to find config of application. TODO: Create separate test configs (maybe testcontainers?)
@ExtendWith(SpringExtension::class)
//Disable the replacement of mysql datasource with h2
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryTest {

    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var customerRepository: CustomerRepository


    @Test
    fun WhenFindById_thenReturnCustomer() {
        val customer= Customer("Test","Testing","Testheim")
        entityManager.persist(customer)
        entityManager.flush()
        val customerFound = customerRepository.findByIdOrNull(customer.id)
        assert(customerFound == customer)
    }

    @Test
    fun WhenDeleteCustomer_thenReturnNull(){
        val customer= Customer("Test","Testing","Testheim")
        entityManager.persist(customer)
        entityManager.flush()
        customerRepository.deleteById(customer.id)
        val deletedCustomer = customerRepository.findByIdOrNull(customer.id)
        assert(deletedCustomer == null)
    }

}
