package eda.shoppingBasket.service

import com.google.gson.Gson
import eda.shoppingBasket.service.eventing.shoppingBasket.SBOperation
import eda.shoppingBasket.service.eventing.shoppingBasket.ShoppingBasketProducer
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.repository.OfferingRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Header
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.test.context.ActiveProfiles
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class ShoppingBasketEventIntegrationTest: AbstractIntegrationTest() {
    // Latch has to be in CO? Because it doesn't reset otherwise??
    companion object{
        private var sbCountDownLatch = CountDownLatch(1)
        private var message: ShoppingBasketDTO? = null
        private var headers: Array<Header>? = null
        }
    @Autowired
    lateinit var offeringRepository: OfferingRepository

    final val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var producer: ShoppingBasketProducer

    @BeforeEach
    fun setup(){
        logger.info("RESET LATCH")
        message = null
        headers = null
        sbCountDownLatch = CountDownLatch(1)
        offeringRepository.deleteAll()
    }

    @KafkaListener(topics = ["shopping-basket"])
    fun consumeInMessageFormat(pMessage: ConsumerRecord<String,String>){
        logger.info("Consumed message: $pMessage")
        logger.info("Payload: ${pMessage.value()}")
        val dto = Gson().fromJson(pMessage.value(), ShoppingBasketDTO::class.java)
        pMessage.headers().forEach {
            logger.info("Header: ${it.key()} : ${String(it.value())}")
        }
        headers = pMessage.headers().toArray()
        message = dto
        sbCountDownLatch.countDown()
    }

    @Test
    fun testEmissionCreated(){
        sleep(500) //prevent double consumption of events
        val testDto = testBasketDTOEmpty
        producer.sendMessage(testDto, SBOperation.CREATED)
        val consumed = sbCountDownLatch!!.await(3, TimeUnit.SECONDS)
        assert(consumed)
        assert(message!=null && headers != null)
        Assertions.assertEquals(testDto, message)
        Assertions.assertEquals("${SBOperation.CREATED}", String(headers!!.find { it.key().equals("operation") }!!.value()))

    }
    @Test
    fun testEmissionUpdated(){
        sleep(500) //prevent double consumption of events
        val testDto = testBasketDTOEmpty
        producer.sendMessage(testDto, SBOperation.UPDATED)
        val consumed = sbCountDownLatch.await(3, TimeUnit.SECONDS)
        assert(consumed)
        assert(message!=null && headers != null)
        Assertions.assertEquals("${SBOperation.UPDATED}", String(headers!!.find { it.key().equals("operation") }!!.value()))
        Assertions.assertEquals(testDto, message)
    }
}