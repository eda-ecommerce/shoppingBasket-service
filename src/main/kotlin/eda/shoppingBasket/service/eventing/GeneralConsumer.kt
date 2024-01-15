package eda.teamred.service.eventing

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

@Component
class GeneralConsumer {
    private val logger= LoggerFactory.getLogger(this.javaClass)

    var countDownLatch = CountDownLatch(1)
    lateinit var payload: ConsumerRecord<Any, Any>
    var stringData = ""
    @KafkaListener(topics = ["\${spring.kafka.default-topic}"])
    fun firstListener(consumerRecord: ConsumerRecord<Any, Any>){
        logger.info("Message received: [${consumerRecord}]")
        payload = consumerRecord
        stringData = consumerRecord.value().toString()
        countDownLatch.countDown()
    }

    fun resetLatch(){
        countDownLatch = CountDownLatch(1)
    }
}
