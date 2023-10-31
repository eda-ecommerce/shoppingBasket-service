package eda.teamred.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller (private val producer: StringProducer) {
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun sendMessage(
        @RequestBody requestBody: RequestBodyDto
    ){
        producer.sendStringMessage(requestBody.message)
    }
    data class RequestBodyDto(val message: String)
}
