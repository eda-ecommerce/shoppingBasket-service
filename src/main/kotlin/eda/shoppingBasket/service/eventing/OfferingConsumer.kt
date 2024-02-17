package eda.shoppingBasket.service.eventing

import eda.shoppingBasket.service.application.OfferingService
import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.model.entity.Offering
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.Headers
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

@Component
class OfferingConsumer {

    @Autowired
    private lateinit var offeringService: OfferingService

    private val logger= LoggerFactory.getLogger(this.javaClass)

    var countDownLatch = CountDownLatch(1)

    @KafkaListener(topics = ["offering"])
    fun offeringListener(message: Message<OfferingEvent>){
        logger.info("Offering message received: $message")
        if (message.headers.containsKey("operation")){
            val operation = message.headers["operation"]?.toString()
            when(operation){
                "delete" -> {
                    offeringService.deleteOffering(message.payload.id)
                    countDownLatch.countDown()
                    return
                }
                else -> {
                    offeringService.saveOffering(message.payload)
                    countDownLatch.countDown()
                    return
                }
            }
        }
    }

    fun resetLatch(){
        countDownLatch = CountDownLatch(1)
    }

}