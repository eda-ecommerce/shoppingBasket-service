package eda.teamred.service

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class StringConsumer {
    private val logger= LoggerFactory.getLogger(this.javaClass)

    @KafkaListener(topics = [TOPIC_NAME], groupId = GROUP_ID)
    fun firstListener(message: String){
        logger.info("Message received: [$message]")
    }
}
