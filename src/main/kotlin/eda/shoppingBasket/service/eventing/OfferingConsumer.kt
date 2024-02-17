package eda.shoppingBasket.service.eventing

import com.google.gson.Gson
import eda.shoppingBasket.service.application.OfferingService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Header
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

    //@KafkaListener(topics = ["offering"])
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
    @KafkaListener(topics = ["offering"])
    fun offeringJsonListener(message: ConsumerRecord<Any, Any>){
        logger.info("Offering message received: ${message.value()}")
        val payload =  message.value().toString()
        val event = Gson().fromJson(payload, OfferingEvent::class.java)
        val headers = message.headers()
        headers.forEach { header: Header ->
            logger.info("Header: ${header.key()} : ${header.value()}")
            when (header.key()) {
                "operation" -> {
                    val operation = header.value().toString()
                    when(operation){
                        "delete" -> {
                            offeringService.deleteOffering(event.id)
                            countDownLatch.countDown()
                            return
                        }
                        else -> {
                            offeringService.saveOffering(event)
                            countDownLatch.countDown()
                            return
                        }
                    }
                }
            }
        }
    }

    fun resetLatch(){
        countDownLatch = CountDownLatch(1)
    }

}