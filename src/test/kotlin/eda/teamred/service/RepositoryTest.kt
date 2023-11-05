package eda.teamred.service

import eda.teamred.dto.CustomerDTO
import eda.teamred.repository.CustomerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.testng.annotations.Test
import java.util.*

@DataJpaTest
class RepositoryTest {

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var customerRepository : CustomerRepository

    @Test
    fun findByIdTest(){
        val uuid = UUID.randomUUID()

        val customer = CustomerDTO(uuid, "Hans", "Peter", "Somewhere")
        testEntityManager.persist(customer)
        testEntityManager.flush()
        val found = customerRepository.findById(uuid)

        assert(found.equals(customer))
    }

}