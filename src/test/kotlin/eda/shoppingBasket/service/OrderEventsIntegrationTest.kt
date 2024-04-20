package eda.shoppingBasket.service

import eda.shoppingBasket.service.application.ShoppingBasketService
import eda.shoppingBasket.service.eventing.offering.OfferingConsumer
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.model.entity.ItemState
import eda.shoppingBasket.service.model.entity.Offering
import eda.shoppingBasket.service.repository.OfferingRepository
import eda.shoppingBasket.service.repository.ShoppingBasketItemRepository
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.MySQLContainer
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("dev")
class OrderEventsIntegrationTest {

    companion object{
        val db = MySQLContainer("mysql")

        @JvmStatic
        @BeforeAll
        fun startDBContainer(){
            db.start()
        }

        @JvmStatic
        @AfterAll
        fun stopDBContainer(){
            db.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDbContainer(registry: DynamicPropertyRegistry){
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }
    @Test
    fun testDbRunning(){
        assert(db.isRunning)
    }

    @Autowired
    private lateinit var offeringConsumer: OfferingConsumer

    @Autowired
    private lateinit var offeringRepository: OfferingRepository

    @Autowired
    private lateinit var shoppingBasketItemRepository: ShoppingBasketItemRepository

    @Autowired
    private lateinit var shoppingBasketService: ShoppingBasketService

    @Autowired
    private lateinit var producer: KafkaTemplate<String, String>

    @BeforeEach
    fun setup(){
        offeringConsumer.resetLatch()
    }
    @Test
    fun testOfferingUnavailableEvent_UpdatesItemState(){
        offeringRepository.save(Offering(
            offeringID = UUID.fromString("ed672bdf-831c-437f-ad41-d89ac2f398a4"),
            price = 10.0f,
            quantity = 5
        ))
        val sb = shoppingBasketService.createShoppingBasket(ShoppingBasketDTO(
            null,null
        ))
        val updatedSb = shoppingBasketService.addOfferingToShoppingBasket(sb.shoppingBasketId!!, UUID.fromString("ed672bdf-831c-437f-ad41-d89ac2f398a4"), 1)
        Assertions.assertEquals(1, updatedSb!!.items.size)
        val items = shoppingBasketItemRepository.findAllByOfferingID(UUID.fromString("ed672bdf-831c-437f-ad41-d89ac2f398a4"))
        Assertions.assertEquals(1, items.size)
        val eventString = "{\n" +
                "    \"id\": \"ed672bdf-831c-437f-ad41-d89ac2f398a4\",\n" +
                "    \"status\": \"active\",\n" +
                "    \"quantity\": 5,\n" +
                "    \"price\": 10,\n" +
                "    \"product\": {\n" +
                "        \"id\": \"5de3949d-b496-4aa8-a99b-3d806b8e347f\",\n" +
                "        \"status\": \"retired\"\n" +
                "    }\n" +
                "}"
        val retireOfferingRecord = ProducerRecord<String, String>("offering", eventString).apply {
            headers().add("operation", "deleted".toByteArray())
            headers().add("source", "offering-service".toByteArray())
            headers().add("timestamp", System.currentTimeMillis().toString().toByteArray())
        }
        producer.send(retireOfferingRecord).join()
        val consumed = offeringConsumer.countDownLatch.await(3, TimeUnit.SECONDS)
        assert(consumed)
        val updatedItems = shoppingBasketItemRepository.findAllByOfferingID(UUID.fromString("ed672bdf-831c-437f-ad41-d89ac2f398a4"))
        Assertions.assertTrue(updatedItems.isNotEmpty())
        Assertions.assertEquals(ItemState.UNAVAILABLE,updatedItems.first().state)
    }
    @Test
    fun consumeOfferingMessage_savesCorrectOffering(){
        val eventString = "{\n" +
                "    \"id\": \"ed672bdf-831c-437f-ad41-d89ac2f398a4\",\n" +
                "    \"status\": \"active\",\n" +
                "    \"quantity\": 5,\n" +
                "    \"price\": 10,\n" +
                "    \"product\": {\n" +
                "        \"id\": \"5de3949d-b496-4aa8-a99b-3d806b8e347f\",\n" +
                "        \"status\": \"active\"\n" +
                "    }\n" +
                "}"

        val createOfferingRecord = ProducerRecord<String, String>("offering", eventString).apply {
            headers().add("operation", "create".toByteArray())
            headers().add("source", "offering-service".toByteArray())
            headers().add("timestamp", System.currentTimeMillis().toString().toByteArray())
        }
        producer.send(createOfferingRecord).join()
        val consumed = offeringConsumer.countDownLatch.await(10, TimeUnit.SECONDS)
        assert(consumed)
        val savedOffering = offeringRepository.findById(UUID.fromString("ed672bdf-831c-437f-ad41-d89ac2f398a4"))
        Assertions.assertEquals(UUID.fromString("ed672bdf-831c-437f-ad41-d89ac2f398a4"), savedOffering.get().offeringID)
    }
}