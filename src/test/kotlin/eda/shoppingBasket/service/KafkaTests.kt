package eda.shoppingBasket.service

import com.ninjasquad.springmockk.MockkBean
import eda.shoppingBasket.service.application.OfferingService
import eda.shoppingBasket.service.application.ShoppingBasketService
import eda.shoppingBasket.service.eventing.OfferingConsumer
import eda.shoppingBasket.service.eventing.SBOperation
import eda.shoppingBasket.service.eventing.ShoppingBasketProducer
import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@ExtendWith(SpringExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class KafkaTests {

    @Autowired
    private lateinit var offeringConsumer: OfferingConsumer

    @MockkBean
    lateinit var shoppingBasketService: ShoppingBasketService

    final val logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @MockkBean
    lateinit var offeringService: OfferingService

    @Autowired
    lateinit var template: KafkaTemplate<String, OfferingDTO>

    @Autowired
    private lateinit var producer: ShoppingBasketProducer

    @BeforeEach
    fun setup(){
        sbCountDownLatch = CountDownLatch(1)
        offeringConsumer.resetLatch()
        every { offeringService.saveOffering(any()) } returns Unit
    }

    var sbCountDownLatch = CountDownLatch(1)
    var message: Message<ShoppingBasketDTO>? = null

    @KafkaListener(topics = ["shopping-basket"])
    fun consumeInMessageFormat(pMessage: Message<ShoppingBasketDTO>){
        logger.info("Consumed message: $pMessage")
        logger.info("Payload: ${pMessage.payload}")
        pMessage.headers.forEach {
            logger.info("Header: ${it.key} : ${it.value}")
        }
        sbCountDownLatch.countDown()
        message = pMessage
    }

    @Test
    fun testEmissionCreated(){
        val testDto = ShoppingBasketDTO(
            shoppingBasketID = UUID.randomUUID(),
            customerID = UUID.randomUUID(),
            totalPrice = 0.0f
        )
        producer.sendMessage(testDto, SBOperation.CREATE)
        val consumed = sbCountDownLatch.await(10, TimeUnit.SECONDS)
        assert(consumed)
        assert(message!=null && message!!.headers["operation"] != null)
        Assertions.assertEquals("${SBOperation.CREATE}", message!!.headers["operation"])
        Assertions.assertEquals(testDto, message!!.payload)
    }
    @Test
    fun testEmissionUpdated(){
        val testDto = ShoppingBasketDTO(
            shoppingBasketID = UUID.randomUUID(),
            customerID = UUID.randomUUID(),
            totalPrice = 0.0f,
            shoppingBasketItems = mutableListOf(
            )
        )
        producer.sendMessage(testDto, SBOperation.UPDATE)
        val consumed = sbCountDownLatch.await(10, TimeUnit.SECONDS)
        assert(consumed)
        assert(message!=null && message!!.headers["operation"] != null)
        Assertions.assertEquals("${SBOperation.UPDATE}", message!!.headers["operation"])
        Assertions.assertEquals(testDto, message!!.payload)
    }

    @Test
    fun consumeOfferingMessage(){
        val offeringDto = OfferingDTO(
            id = UUID.randomUUID(),
            quantity = 1,
            price = 1f,
            productID = UUID.randomUUID()
        )
        val testMessage: Message<OfferingDTO> = MessageBuilder
            .withPayload(offeringDto)
            .setHeader("topic", "offering")
            .setHeader("operation", "create")
            .setHeader("source", "offering-service")
            .setHeader("!timestamp", System.currentTimeMillis())
            .build()
        template.defaultTopic = "offering"
        template.send(testMessage).join()
        val consumed = offeringConsumer.countDownLatch.await(10, TimeUnit.SECONDS)
        assert(consumed)
        verify { offeringService.saveOffering(any()) }
    }
}