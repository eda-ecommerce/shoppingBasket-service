package eda.shoppingBasket.service.eventing

import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component


const val SHOPPING_BASKET_TOPIC = "shopping-basket"

@Component
class ShoppingBasketProducer(
    private val template: KafkaTemplate<String, ShoppingBasketDTO>,
) {
    fun sendMessage(dto: ShoppingBasketDTO, operation: SBOperation) {
        template.defaultTopic = SHOPPING_BASKET_TOPIC
        val message : Message<ShoppingBasketDTO> = MessageBuilder.withPayload(dto)
            .setHeader("topic", "shopping-basket")
            .setHeader("operation", "$operation")
            .setHeader("source", "shopping-basket-service")
            .build()
        template.send(message)
    }
}