package eda.shoppingBasket.service.eventing

import com.google.gson.Gson
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import org.apache.kafka.common.protocol.types.Field.Str
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component


const val SHOPPING_BASKET_TOPIC = "shopping-basket"

@Component
class ShoppingBasketProducer(
    private val template: KafkaTemplate<String, String>,
) {
    fun sendMessage(dto: ShoppingBasketDTO, operation: SBOperation) {
        template.defaultTopic = SHOPPING_BASKET_TOPIC
        val message : Message<String> = MessageBuilder.withPayload(Gson().toJson(dto))
            .setHeader("topic", "shopping-basket")
            .setHeader("operation", "$operation")
            .setHeader("source", "shopping-basket-service")
            .setHeader("!timestamp", System.currentTimeMillis())
            .build()
        template.send(message)
    }
}