package eda.teamred.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class StringProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    fun sendStringMessage(message: String){
        kafkaTemplate.send(TOPIC_NAME, message)
    }
}
