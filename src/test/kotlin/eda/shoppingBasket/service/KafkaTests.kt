package eda.shoppingBasket.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.ninjasquad.springmockk.MockkBean
import eda.shoppingBasket.service.application.OfferingService
import eda.shoppingBasket.service.application.ShoppingBasketService
import eda.shoppingBasket.service.eventing.OfferingConsumer
import eda.shoppingBasket.service.eventing.OfferingEvent
import eda.shoppingBasket.service.eventing.SBOperation
import eda.shoppingBasket.service.eventing.ShoppingBasketProducer
import eda.shoppingBasket.service.model.OfferingMapper
import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.repository.OfferingRepository
import io.mockk.every
import io.mockk.verify
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.Headers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
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

    @Autowired
    lateinit var offeringRepository: OfferingRepository

    final val logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var offeringService: OfferingService

    @Autowired
    lateinit var template: KafkaTemplate<String, String>

    @Autowired
    private lateinit var producer: ShoppingBasketProducer

    @BeforeEach
    fun setup(){
        sbCountDownLatch = CountDownLatch(1)
        offeringConsumer.resetLatch()
        offeringRepository.deleteAll()
        //every { offeringService.saveOffering(any()) } returns Unit
    }

    var sbCountDownLatch = CountDownLatch(1)
    var message: ShoppingBasketDTO? = null
    var headers: Array<Header>? = null

    @KafkaListener(topics = ["shopping-basket"])
    fun consumeInMessageFormat(pMessage: ConsumerRecord<String,String>){
        logger.info("Consumed message: $pMessage")
        logger.info("Payload: ${pMessage.value()}")
        val dto = Gson().fromJson(pMessage.value(), ShoppingBasketDTO::class.java)
        pMessage.headers().forEach {
            logger.info("Header: ${it.key()} : ${it.value()}")
        }
        headers = pMessage.headers().toArray()
        sbCountDownLatch.countDown()
        message = dto
    }

    @Test
    fun testEmissionCreated(){
        val testDto = ShoppingBasketDTO(
            shoppingBasketId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            totalPrice = 0.0f
        )
        producer.sendMessage(testDto, SBOperation.CREATED)
        val consumed = sbCountDownLatch.await(10, TimeUnit.SECONDS)
        assert(consumed)
        assert(message!=null && headers != null)
        Assertions.assertEquals("${SBOperation.CREATED}", String(headers!!.find { it.key().equals("operation") }!!.value()))
        Assertions.assertEquals(testDto, message)
    }
    @Test
    fun testEmissionUpdated(){
        val testDto = ShoppingBasketDTO(
            shoppingBasketId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            totalPrice = 0.0f,
            items = mutableListOf(
            )
        )
        producer.sendMessage(testDto, SBOperation.UPDATED)
        val consumed = sbCountDownLatch.await(10, TimeUnit.SECONDS)
        assert(consumed)
        assert(message!=null && headers != null)
        Assertions.assertEquals("${SBOperation.UPDATED}", String(headers!!.find { it.key().equals("operation") }!!.value()))
        Assertions.assertEquals(testDto, message)
    }

    @Test
    fun consumeOfferingMessage(){
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

        val testRecord = ProducerRecord<String, String>("offering", eventString).apply {
            headers().add("operation", "create".toByteArray())
            headers().add("source", "offering-service".toByteArray())
            headers().add("timestamp", System.currentTimeMillis().toString().toByteArray())
        }
        template.send(testRecord).join()
        val consumed = offeringConsumer.countDownLatch.await(10, TimeUnit.SECONDS)
        assert(consumed)
        val savedOffering = offeringRepository.findById(UUID.fromString("ed672bdf-831c-437f-ad41-d89ac2f398a4"))
        Assertions.assertEquals(UUID.fromString("ed672bdf-831c-437f-ad41-d89ac2f398a4"), savedOffering.get().offeringID)
    }
}