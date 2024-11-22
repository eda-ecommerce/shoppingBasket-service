package eda.shoppingBasket.service

import eda.shoppingBasket.service.repository.ShoppingBasketItemRepository
import eda.shoppingBasket.service.repository.ShoppingBasketRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class ShoppingBasketIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    lateinit var shoppingBasketRepository: ShoppingBasketRepository

    @Autowired
    lateinit var itemRepository: ShoppingBasketItemRepository

    @BeforeEach
    fun setUp() {
        shoppingBasketRepository.deleteAll()
    }

    @Test
    fun `removing Items should save empty Basket to DB`() {
        val basket = testBasketFull
        shoppingBasketRepository.save(basket)
        basket.removeItemFromBasket(testItem1.id)
        basket.removeItemFromBasket(testItem2.id)
        shoppingBasketRepository.save(basket)
        val result = shoppingBasketRepository.findByIdOrNull(testBasketFull.id)
        Assertions.assertEquals(testBasketEmpty, result)
    }

    @Test
    fun `adding offerings to Basket automatically creates Items and saves everything to DB`() {
        // Given
        val basket = testBasketEmpty
        // When
        basket.addOfferingToBasket(testOffering1, 10)
        shoppingBasketRepository.save(basket)
        val result = shoppingBasketRepository.findByIdOrNull(testBasketEmpty.id)
        val items = itemRepository.findAll()
        // Then
        Assertions.assertEquals(testBasketFull, result)
        Assertions.assertEquals(1, items.count())
        Assertions.assertEquals(offeringID1, items.first().offeringID)
    }

    @Test
    fun `adding the same Offering modifies quantity instead`() {
        val basket = testBasketEmpty
        shoppingBasketRepository.save(basket)
        basket.addOfferingToBasket(testOffering1, 5)
        basket.addOfferingToBasket(testOffering1, 10)
        shoppingBasketRepository.save(basket)
        val result = shoppingBasketRepository.findByIdOrNull(testBasketFull.id)
        val items = result!!.items
        Assertions.assertEquals(1, items.count())
        val item1 = items.find { it.offeringID == testItem1.offeringID }
        Assertions.assertEquals(15, item1!!.quantity)
        Assertions.assertEquals(15.0, item1.subtotal)
    }

    @Test
    fun `modifying quantity of an Item should update the DB`() {
        val basket = testBasketFull
        shoppingBasketRepository.save(basket)
        basket.updateItemQuantity(testItem1.id, 10)
        shoppingBasketRepository.save(basket)
        val result = shoppingBasketRepository.findByIdOrNull(testBasketFull.id)
        val items = result!!.items
        val item1 = items.find { it.id == testItem1.id }
        Assertions.assertEquals(10, item1!!.quantity)
        Assertions.assertEquals(10.0, item1.subtotal)
    }

    @Test
    fun `modifying missing item should throw exception`() {
        val basket = testBasketEmpty
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            basket.updateItemQuantity(testItem1.id, 10)
        }
    }

    @Test
    fun `removing missing item should throw exception`() {
        val basket = testBasketEmpty
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            basket.removeItemFromBasket(testItem1.id)
        }
    }

    @Test
    fun `readyToCheckout should return false if any item is unavailable`() {
        val basket = testBasketFullNotReady
        Assertions.assertEquals(false, basket.readyToCheckout())
    }

    @Test
    fun `readyToCheckout should return true if all items are available`() {
        val basket = testBasketFull
        Assertions.assertEquals(true, basket.readyToCheckout())
    }

}