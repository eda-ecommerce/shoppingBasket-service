package eda.shoppingBasket.service.eventing

import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

@Component
class TestShoppingBasketConsumer {
    private val logger= LoggerFactory.getLogger(this.javaClass)

    var countDownLatch = CountDownLatch(1)
    var message: Message<ShoppingBasketDTO>? = null

    @KafkaListener(topics = ["shopping-basket"])
    fun consumeInMessageFormat(pMessage: Message<ShoppingBasketDTO>){
        logger.info("Consumed message: $pMessage")
        logger.info("Payload: ${pMessage.payload}")
        pMessage.headers.forEach {
            logger.info("Header: ${it.key} : ${it.value}")
        }
        countDownLatch.countDown()
        message = pMessage
    }

    fun resetLatch(){
        countDownLatch = CountDownLatch(1)
    }

}