package eda.shoppingBasket.service.eventing.offering

import com.google.gson.Gson
import eda.shoppingBasket.service.application.OfferingService
import jakarta.transaction.Transactional
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

@Component
class OfferingConsumer {

    @Autowired
    private lateinit var offeringService: OfferingService

    private val logger = LoggerFactory.getLogger(this.javaClass)

    var countDownLatch = CountDownLatch(1)

    @KafkaListener(topics = ["offering"], )
    @Transactional
    fun offeringJsonListener(message: ConsumerRecord<String, String>, acknowledgment: Acknowledgment) {
        logger.info("Offering message received: ${message.value()}, at timestamp: ${message.timestamp()}")
        val event = Gson().fromJson(message.value(), OfferingEvent::class.java)
        val operationHeader = String(message.headers().lastHeader("operation").value())
        logger.info("Operation: $operationHeader")
        when (operationHeader) {
            "deleted" -> {
                offeringService.disableOffering(event.id)
                acknowledgment.acknowledge()
                countDownLatch.countDown()
            }
            else -> {
                offeringService.saveOffering(event)
                acknowledgment.acknowledge()
                countDownLatch.countDown()
            }
        }
        countDownLatch = CountDownLatch(1)
    }
}