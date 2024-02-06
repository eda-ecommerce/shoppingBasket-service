package eda.shoppingBasket.service

import eda.shoppingBasket.service.model.dto.OfferingInBasketDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketItemDTO
import eda.shoppingBasket.service.model.entity.Offering
import eda.shoppingBasket.service.model.entity.ShoppingBasket
import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import eda.shoppingBasket.service.repository.OfferingRepository
import eda.shoppingBasket.service.repository.ShoppingBasketItemRepository
import eda.shoppingBasket.service.repository.ShoppingBasketRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
class ControllerIntegrationTest {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var shoppingBasketRepository: ShoppingBasketRepository

    @Autowired
    lateinit var itemRepository: ShoppingBasketItemRepository

    @Autowired
    lateinit var offeringRepository: OfferingRepository

    private final val globalShoppingBasketID = randomUUID()
    private final val globalOfferingID = randomUUID()
    private final val globalCustomerID = randomUUID()

    final val emptyTestShoppingBasket = ShoppingBasket(
        shoppingBasketID = globalShoppingBasketID,
        customerID = globalCustomerID,
        totalPrice = 0.0f
    )

    final val fullTestShoppingBasket = ShoppingBasket(
        shoppingBasketID = globalShoppingBasketID,
        customerID = globalCustomerID,
        totalPrice = 16f
    )

    final val testOffering = Offering(
        offeringID = globalOfferingID,
        quantity = 1,
        price = 4f
    )

    final val testShoppingBasketItem = ShoppingBasketItem(
        shoppingBasket = fullTestShoppingBasket,
        shoppingBasketItemID = randomUUID(),
        quantity = 4,
        totalPrice = 16f,
        offeringID = testOffering.offeringID
    )

    final val testShoppingBasketItemDTO = ShoppingBasketItemDTO(
        shoppingBasketID = testShoppingBasketItem.shoppingBasket.shoppingBasketID,
        offeringID = testShoppingBasketItem.offeringID,
        totalPrice = testShoppingBasketItem.totalPrice,
        itemState = testShoppingBasketItem.state,
        quantity = testShoppingBasketItem.quantity
    )

    final val fullTestShoppingBasketDTO = ShoppingBasketDTO(
        shoppingBasketID = fullTestShoppingBasket.shoppingBasketID,
        customerID = fullTestShoppingBasket.customerID,
        totalPrice = fullTestShoppingBasket.totalPrice,
        totalItemQuantity = 1,
        shoppingBasketItems = mutableListOf(testShoppingBasketItemDTO)
    )
    final val emptyTestShoppingBasketDTO = ShoppingBasketDTO(
        shoppingBasketID = emptyTestShoppingBasket.shoppingBasketID,
        customerID = emptyTestShoppingBasket.customerID,
        totalPrice = emptyTestShoppingBasket.totalPrice,
        totalItemQuantity = 0,
        shoppingBasketItems = mutableListOf()
    )

    @BeforeEach
    fun setup() {
        itemRepository.deleteAll()
        shoppingBasketRepository.deleteAll()
        offeringRepository.deleteAll()
    }

    @Test
    fun `test get shopping basket by shopping basket id`() {
        shoppingBasketRepository.save(emptyTestShoppingBasket)
        val response = testRestTemplate.getForEntity("/shoppingBasket/${emptyTestShoppingBasket.shoppingBasketID}", ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, emptyTestShoppingBasketDTO)
    }

    @Test
    fun `test get shopping basket by customer id`() {
        shoppingBasketRepository.save(emptyTestShoppingBasket)
        val response = testRestTemplate.getForEntity("/shoppingBasket?customerID=${emptyTestShoppingBasket.customerID}", ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, emptyTestShoppingBasketDTO)
    }

    @Test
    fun `test create shopping basket`() {
        offeringRepository.save(testOffering)
        val response = testRestTemplate.postForEntity("/shoppingBasket", fullTestShoppingBasketDTO, ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, fullTestShoppingBasketDTO)
    }
    //Disabled due to use case
    fun `test create shopping basket with customer id`() {
        val response = testRestTemplate.postForEntity("/shoppingBasket/customer/${fullTestShoppingBasket.customerID}", fullTestShoppingBasketDTO, ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, fullTestShoppingBasketDTO)
    }

    @Test
    fun `test add an offering to shopping basket`() {
        shoppingBasketRepository.save(emptyTestShoppingBasket)
        offeringRepository.save(testOffering)
        val offeringInBasketDTO = OfferingInBasketDTO(
            offeringID = testOffering.offeringID,
            quantity = 4
        )
        val response = testRestTemplate.postForEntity("/shoppingBasket/${emptyTestShoppingBasket.shoppingBasketID}/addOffering", offeringInBasketDTO, ShoppingBasketDTO::class.java)
        assert(response != null)
        Assertions.assertEquals(HttpStatus.CREATED,response.statusCode)
        assert(response.hasBody())
        println(response.body?.shoppingBasketItems)
        assertDtoEqualsDto(response.body!!, fullTestShoppingBasketDTO)
    }

    @Test
    fun `test modify the quantity of an offering`() {
        offeringRepository.save(testOffering)
        shoppingBasketRepository.save(fullTestShoppingBasket)
        itemRepository.save(testShoppingBasketItem)
        val offeringInBasketDTO = OfferingInBasketDTO(
            offeringID = testOffering.offeringID,
            quantity = 1
        )
        val response = testRestTemplate.postForEntity("/shoppingBasket/${emptyTestShoppingBasket.shoppingBasketID}/items/${testShoppingBasketItem.shoppingBasketItemID}/changeQuantity", offeringInBasketDTO, ShoppingBasketDTO::class.java)
        assert(response != null)
        Assertions.assertEquals(HttpStatus.OK,response.statusCode)
        assert(response.hasBody())
        assertDtoEqualsDto(response.body!!, fullTestShoppingBasketDTO.apply {
            totalItemQuantity = 1
            totalPrice = 4f
        })
    }

    @Test
    fun `test remove an offering from a shopping basket`() {
        offeringRepository.save(testOffering)
        shoppingBasketRepository.save(fullTestShoppingBasket)
        itemRepository.save(testShoppingBasketItem)
        val resp: ResponseEntity<Void> = testRestTemplate.exchange(
            "/shoppingBasket/${fullTestShoppingBasket.shoppingBasketID}/items/${testShoppingBasketItem.shoppingBasketItemID}", HttpMethod.DELETE, HttpEntity.EMPTY,
            Void::class.java
        )
        Assertions.assertEquals(HttpStatus.OK,resp.statusCode)
        Assertions.assertNull(itemRepository.findByIdOrNull(testShoppingBasketItem.shoppingBasketItemID))
    }

    @Test
    fun `test delete shopping basket by shopping basket id`() {
        shoppingBasketRepository.save(fullTestShoppingBasket)
        testRestTemplate.delete("/shoppingBasket/${fullTestShoppingBasket.shoppingBasketID}")
        val response = testRestTemplate.getForEntity("/shoppingBasket/${fullTestShoppingBasket.shoppingBasketID}", ShoppingBasketDTO::class.java)
        assert(response != null)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.hasBody())
    }

    fun assertDtoEqualsDto(actual: ShoppingBasketDTO, expected: ShoppingBasketDTO) {
        Assertions.assertEquals(expected.shoppingBasketID , actual.shoppingBasketID)
        Assertions.assertEquals(expected.customerID , actual.customerID)
        Assertions.assertEquals(expected.totalPrice , actual.totalPrice)
        Assertions.assertEquals(expected.totalItemQuantity , actual.totalItemQuantity)
        Assertions.assertEquals(expected.shoppingBasketItems , actual.shoppingBasketItems)
    }

}