package eda.shoppingBasket.service

import com.ninjasquad.springmockk.MockkBean
import eda.shoppingBasket.service.application.ShoppingBasketService
import eda.shoppingBasket.service.repository.ShoppingBasketRepository
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("unit-test")
@EmbeddedKafka(topics = ["shopping-basket"], partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
class ShoppingBasketServiceUnitTest {

    @MockkBean
    lateinit var sbRepository: ShoppingBasketRepository

    @Autowired
    lateinit var sbService: ShoppingBasketService

    @Test
    fun getShoppingBasket_returnsDTO(){
        // Given
        every { sbRepository.findByIdOrNull(basketID) } returns testBasketFull
        // When
        val result = sbService.getShoppingBasket(basketID)
        // Then
        assert(result == testBasketDTOFull)
    }

    @Test
    fun getShoppingBasketByCustomerID_returnsDTO(){
        TODO()
    }

    @Test
    fun createShoppingBasket_returnsDTO(){
        TODO()
    }

    @Test
    fun addOfferingToBasket_returnsDTO(){
        TODO()
    }

    @Test
    fun addOfferingToBasket_throwsException(){
        TODO()
    }

    @Test
    fun modifyItemQuantity_returnsDTO(){
        TODO()
    }

    @Test
    fun modifyItemQuantity_throwsException(){
        TODO()
    }

    @Test
    fun modifyItemQuantity_throwsException2(){
        TODO()
    }

}