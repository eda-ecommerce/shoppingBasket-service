package eda.shoppingBasket.service.eventing

import com.google.gson.Gson
import eda.shoppingBasket.service.application.OfferingService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Header
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

@Component
class OfferingConsumer {

    @Autowired
    private lateinit var offeringService: OfferingService

    private val logger = LoggerFactory.getLogger(this.javaClass)

    var countDownLatch = CountDownLatch(1)

    @KafkaListener(topics = ["offering"])
    fun offeringJsonListener(message: ConsumerRecord<Any, Any>) {
        logger.info("Offering message received: ${message.value()}")
        val payload = message.value().toString()
        val event = Gson().fromJson(payload, OfferingEvent::class.java)
        val headers = message.headers()
        headers.forEach { header: Header ->
            logger.info("Header: ${header.key()} : ${String(header.value())}")
            if (header.key() == "operation") {
                val operation = String(header.value())
                when (operation) {
                    "delete" -> {
                        offeringService.disableOffering(event.id)
                        countDownLatch.countDown()
                        return
                    }
                    else -> {
                        offeringService.saveOffering(event)
                        countDownLatch.countDown()
                        return
                    }
                    //Future: call reEnableOffering(event)
                }
            }
        }
        countDownLatch.countDown()
    }

    fun resetLatch() {
        countDownLatch = CountDownLatch(1)
    }

}