package eda.teamred.service.eventing

import com.google.gson.Gson
import com.google.gson.JsonElement
import eda.shoppingBasket.service.dto.DTO
import java.time.LocalDateTime

class GeneralEvent
    (
    val type: EventType,
    payload: DTO,
    val source: String = "customer-service",
    val timestamp: String = LocalDateTime.now().toString(),
) {
    val payload: JsonElement
    init {
        this.payload = Gson().toJsonTree(payload)
    }
    override fun toString(): String{ // HAS TO LOOK LIKE JSON STRING
        return Gson().toJson(this)
    }
}
