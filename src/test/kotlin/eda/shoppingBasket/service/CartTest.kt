package eda.shoppingBasket.service

import eda.shoppingBasket.service.entity.ShoppingBasket
import eda.shoppingBasket.service.entity.ShoppingBasketItem
import eda.shoppingBasket.service.repository.ShoppingBasketItemRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.*
import java.util.UUID.randomUUID

class CartTest {

    private val shoppingBasketItemRepository: ShoppingBasketItemRepository = mockk()

    private val testItemUUID = randomUUID()
    private val testBasketUUID = randomUUID()

    private val testShoppingBasket = ShoppingBasket(
        shoppingBasketID = testBasketUUID,
        customerID = randomUUID(),
        totalPrice = 0.0f
    )

    private val testItem = ShoppingBasketItem(
        shoppingBasket = testShoppingBasket,
        quantity = 1,
        itemPrice = 1.0f
    )

    init {
        every { shoppingBasketItemRepository.findById(testItemUUID) } returns Optional.of(testItem)
    }
    @Test
    fun testCart() {

    }


}