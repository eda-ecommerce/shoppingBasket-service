package eda.teamred.service

import eda.teamred.service.dto.CustomerDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

//@DataJpaTest
//Annotate to find config of application. TODO: Create separate test configs (maybe testcontainers?)
@ExtendWith(SpringExtension::class)
//Disable the replacement of mysql datasource with h2
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun testController(){
        val result = testRestTemplate.postForEntity("/customer", CustomerDTO("Test1","Test2", "TestAddress"), UUID::class.java)
        assert(result != null)
        assert(result.statusCode == HttpStatus.CREATED)


        val result1 = testRestTemplate.getForEntity("/customer/${result.body}", CustomerDTO::class.java)
        assert(result1 != null)
        assert(result1.statusCode == HttpStatus.OK)
        assert(result1.hasBody())

        val result2 = testRestTemplate.getForEntity("/customer", List::class.java)
        assert(result2 != null)
        assert(result2.statusCode == HttpStatus.OK)
        assert(result2.hasBody())
        assert(result2.body!!.isNotEmpty())

        testRestTemplate.put("/customer/${result.body}", CustomerDTO("UpdateTest","UpdateTest2", "UpdateTestAddress"))

        val result3 = testRestTemplate.getForEntity("/customer/${result.body}", CustomerDTO::class.java)
        assert(result3 != null)
        assert(result3.statusCode == HttpStatus.OK)
        assert(result3.hasBody())
        assert(result3.body!!.firstName == "UpdateTest")

        testRestTemplate.delete("/customer/${result.body}")

        val result4 = testRestTemplate.getForEntity("/customer/${result.body}", CustomerDTO::class.java)
        assert(result4 != null)
        assert(result4.statusCode == HttpStatus.NOT_FOUND)
        assert(!result4.hasBody())
    }

}