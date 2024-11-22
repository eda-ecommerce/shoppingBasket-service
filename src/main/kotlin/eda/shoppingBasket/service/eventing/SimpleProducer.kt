package eda.shoppingBasket.service.eventing

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class SimpleProducer(
    val kafkaTemplate: KafkaTemplate<String, String>
) {
    fun sendMessage(topic: String, message: Message<String>){
        kafkaTemplate.defaultTopic = topic
        kafkaTemplate.send(message)
    }
}