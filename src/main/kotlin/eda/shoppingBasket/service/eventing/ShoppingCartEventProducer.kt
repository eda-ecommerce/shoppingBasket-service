package eda.teamred.service.eventing

import eda.shoppingBasket.service.dto.DTO
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ShoppingCartEventProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {
    fun emitEvent(eventType: EventType, dto: DTO, topic: String = "\${spring.kafka.default-topic}") {
        val event = GeneralEvent(eventType, dto)
        val eventString = event.toString()
        kafkaTemplate.send(topic, eventString)
    }
}
