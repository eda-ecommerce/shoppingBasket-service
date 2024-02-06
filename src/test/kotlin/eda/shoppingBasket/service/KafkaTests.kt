package eda.shoppingBasket.service

import eda.shoppingBasket.service.application.OfferingService
import eda.shoppingBasket.service.eventing.OfferingConsumer
import eda.shoppingBasket.service.eventing.SBOperation
import eda.shoppingBasket.service.eventing.ShoppingBasketProducer
import eda.shoppingBasket.service.eventing.TestShoppingBasketConsumer
import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
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
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@ExtendWith(SpringExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class KafkaTests {
    @Autowired
    private lateinit var consumer: TestShoppingBasketConsumer

    @Autowired
    private lateinit var offeringConsumer: OfferingConsumer

    @Autowired
    lateinit var offeringService: OfferingService

    @Autowired
    lateinit var template: KafkaTemplate<String, OfferingDTO>

    @Autowired
    private lateinit var producer: ShoppingBasketProducer

    @KafkaListener(topics = ["shopping-basket"], groupId = "shopping-basket-service")
    fun consume(message: ShoppingBasketDTO) {
        println("Consumed message: $message")
        throw Exception("Test")
    }

    @Test
    fun testEmission(){
        producer.sendMessage(ShoppingBasketDTO(
            shoppingBasketID = UUID.randomUUID(),
            customerID = UUID.randomUUID(),
            totalPrice = 0.0f
        ), SBOperation.CREATE)

        val consumed = consumer.countDownLatch.await(10, TimeUnit.SECONDS)
        assert(consumed)
        assert(consumer.message!=null)

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
            .build()
        template.defaultTopic = "offering"
        template.send(testMessage).join()
        val consumed = offeringConsumer.countDownLatch.await(10, TimeUnit.SECONDS)
        assert(consumed)
        //TODO verify { offeringService.saveOffering(offeringDto) }
    }
}