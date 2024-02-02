package eda.shoppingBasket.service

import eda.shoppingBasket.service.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.entity.Offering
import eda.shoppingBasket.service.entity.ShoppingBasket
import eda.shoppingBasket.service.entity.ShoppingBasketItem
import eda.shoppingBasket.service.repository.ShoppingBasketRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID.randomUUID

//copying stuff from customer-service ControllerIntegrationTest.kt
//Annotate to find config of application. TODO: Create separate test configs (maybe testcontainers?)
@ExtendWith(SpringExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@SpringBootApplication(scanBasePackages = ["eda.shoppingBasket.service"]) //this is needed to autowire the shoppingBasketRepository (at least on my machine)
//TODO: EmbeddedKafka here, maybe something like this:
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
class ControllerIntegrationTest {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var shoppingBasketRepository: ShoppingBasketRepository

    final val testShoppingBasket = ShoppingBasket(
        shoppingBasketID = randomUUID(),
        customerID = randomUUID(),
        totalPrice = 0.0f
    )

    final val testShoppingBasketItem = ShoppingBasketItem(
        shoppingBasket = testShoppingBasket,
        quantity = 4,
        itemPrice = 3.2f
    )

    final val testOffering = Offering(
        offeringID = randomUUID(),
        name = "testOffering",
        unitPrice = 3.2f,
        totalPrice = 6.4f
    )

    final val testShoppingBasketDTO = ShoppingBasketDTO(
        id = randomUUID(),
        shoppingBasketID = testShoppingBasket.shoppingBasketID,
        customerID = testShoppingBasket.customerID,
        totalPrice = testShoppingBasket.totalPrice,
        totalItemQuantity = testShoppingBasket.numberOfItemsInShoppingBasket()
    )

    @BeforeEach
    fun setup() {
        shoppingBasketRepository.deleteAll()
    }

    @Test
    fun `test get shopping basket by shopping basket id`() {
        shoppingBasketRepository.save(testShoppingBasket)
        val response = testRestTemplate.getForEntity("/shoppingBasket/${testShoppingBasket.shoppingBasketID}", ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, testShoppingBasketDTO)
    }

    @Test
    fun `test get shopping basket by customer id`() {
        shoppingBasketRepository.save(testShoppingBasket)
        val response = testRestTemplate.getForEntity("/shoppingBasket/customer/${testShoppingBasket.customerID}", ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, testShoppingBasketDTO)
    }

    @Test
    fun `test create shopping basket`() {
        val response = testRestTemplate.postForEntity("/shoppingBasket", testShoppingBasketDTO, ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, testShoppingBasketDTO)
    }

    @Test
    fun `test create shopping basket with customer id`() {
        val response = testRestTemplate.postForEntity("/shoppingBasket/${testShoppingBasket.customerID}", testShoppingBasketDTO, ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, testShoppingBasketDTO)
    }

    @Test
    fun `test add an offering to shopping basket`() {
        shoppingBasketRepository.save(testShoppingBasket)
        val response = testRestTemplate.postForEntity("/shoppingBasket/${testShoppingBasket.shoppingBasketID}/${testOffering.offeringID}/${testOffering.totalPrice/testOffering.unitPrice}", testOffering, ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, testShoppingBasketDTO)
    }

    @Test
    fun `test modify the quantity of an offering`() {
        shoppingBasketRepository.save(testShoppingBasket)
        val newQuantity = (1..10).random() //for testing, should the max new quantity be 10?
        val response = testRestTemplate.postForEntity("/shoppingBasket/${testShoppingBasket.shoppingBasketID}/${testShoppingBasketItem.shoppingBasketItemID}/${newQuantity}", testShoppingBasketItem, ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, testShoppingBasketDTO)
    }

    @Test
    fun `test remove an offering from a shopping basket`() {
        shoppingBasketRepository.save(testShoppingBasket)
        val response = testRestTemplate.postForEntity("/shoppingBasket/${testShoppingBasket.shoppingBasketID}/${testShoppingBasketItem.shoppingBasketItemID}", testShoppingBasketItem, ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, testShoppingBasketDTO)
    }

    @Test
    fun `test delete shopping basket by shopping basket id`() {
        shoppingBasketRepository.save(testShoppingBasket)
        testRestTemplate.delete("/shoppingBasket/${testShoppingBasket.shoppingBasketID}")
        val response = testRestTemplate.getForEntity("/shoppingBasket/${testShoppingBasket.shoppingBasketID}", ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, testShoppingBasketDTO)
    }

    fun assertDtoEqualsDto(given: ShoppingBasketDTO, expected: ShoppingBasketDTO) {
        assert(given.shoppingBasketID == expected.shoppingBasketID)
        assert(given.customerID == expected.customerID)
        assert(given.totalPrice == expected.totalPrice)
        assert(given.totalItemQuantity == expected.totalItemQuantity)
    }

}