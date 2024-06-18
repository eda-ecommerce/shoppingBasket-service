package eda.shoppingBasket.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eda.shoppingBasket.service.eventing.SimpleProducer
import eda.shoppingBasket.service.eventing.offering.OfferingConsumer
import eda.shoppingBasket.service.model.entity.Offering
import eda.shoppingBasket.service.repository.OfferingRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("dev")
class OfferingConsumerIntegrationTest: AbstractIntegrationTest() {
    @Autowired
    lateinit var offeringConsumer: OfferingConsumer

    @Autowired
    private lateinit var simpleProducer: SimpleProducer

    @Autowired
    lateinit var offeringRepository: OfferingRepository

    @Test
    fun testOfferingConsumer() {
        val offeringEvent = Offering(1, 1.0, Offering.Status.ACTIVE)
        val message: Message<String> = MessageBuilder.withPayload(jacksonObjectMapper().writeValueAsString(offeringEvent))
            .setHeader("operation", "created")
            .build()
        simpleProducer.sendMessage("offering", message)
        offeringConsumer.countDownLatch.await()
        assertEquals(1, offeringRepository.count())
    }
}