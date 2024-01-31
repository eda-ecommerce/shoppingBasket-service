package eda.shoppingBasket.service

import com.ninjasquad.springmockk.MockkBean
import eda.shoppingBasket.service.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.entity.Offering
import eda.shoppingBasket.service.entity.ShoppingBasket
import eda.shoppingBasket.service.entity.ShoppingBasketItem
import eda.shoppingBasket.service.repository.OfferingRepository
import eda.shoppingBasket.service.repository.ShoppingBasketItemRepository
import eda.shoppingBasket.service.repository.ShoppingBasketRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import java.util.UUID.randomUUID

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("unit-test")
class CartApplicationServiceTest {

    @MockkBean
    lateinit var shoppingBasketRepository: ShoppingBasketRepository

    @MockkBean
    lateinit var shoppingBasketItemRepository: ShoppingBasketItemRepository

    @MockkBean
    lateinit var offeringRepository: OfferingRepository

    @Autowired
    lateinit var shoppingBasketService: ShoppingBasketService

    @Autowired
    final lateinit var shoppingBasketMapper: ShoppingBasketMapper

    private val testShoppingBasketItemUUID = randomUUID()
    private val testShoppingBasketUUID = randomUUID()
    private val testCustomerUUID = randomUUID()
    private val testOfferingUUID = randomUUID()

    @BeforeEach
    fun setup() {
        shoppingBasketRepository.deleteAll()
    }

    final val testShoppingBasket = ShoppingBasket(
        shoppingBasketID = testShoppingBasketUUID,
        customerID = testCustomerUUID,
        totalPrice = 0.0f
    )

    val testShoppingBasketDTO = shoppingBasketMapper.toDTO(testShoppingBasket)

    final val testShoppingBasketItem = ShoppingBasketItem(
        shoppingBasketItemID = testShoppingBasketItemUUID,
        shoppingBasket = testShoppingBasket,
        quantity = 3,
        itemPrice = 4.2f
    )

    final val testOffering = Offering(
        offeringID = testOfferingUUID,
        name = "testOffering",
        unitPrice = 5.6f,
        totalPrice = 5.6f
    )

    fun equalsTestShoppingBasketDto(givenDTO: ShoppingBasketDTO) {
        assert(givenDTO.id == testShoppingBasketDTO.id)
        assert(givenDTO.shoppingBasketID == testShoppingBasketDTO.shoppingBasketID)
        assert(givenDTO.customerID == testShoppingBasketDTO.customerID)
        assert(givenDTO.totalPrice == testShoppingBasketDTO.totalPrice)
        assert(givenDTO.totalItemQuantity == testShoppingBasketDTO.totalItemQuantity)
    }

    fun equalsTestShoppingBasket(givenShoppingBasket: ShoppingBasket) {
        assert(givenShoppingBasket.shoppingBasketID == testShoppingBasket.shoppingBasketID)
        assert(givenShoppingBasket.customerID == testShoppingBasket.customerID)
        assert(givenShoppingBasket.totalPrice == testShoppingBasket.totalPrice)
    }

    init {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        every { shoppingBasketItemRepository.findById(testShoppingBasketItemUUID) } returns Optional.of(testShoppingBasketItem)
        every { offeringRepository.findById(testOfferingUUID) } returns Optional.of(testOffering)
    }

    fun createShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.save(testShoppingBasket) } returns testShoppingBasket
        val result = shoppingBasketService.createShoppingBasket(testShoppingBasketDTO)
        equalsTestShoppingBasketDto(result)
    }

    fun createShoppingBasketWithCustomerIDShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.save(testShoppingBasket) } returns testShoppingBasket
        val result = shoppingBasketService.createShoppingBasketWithCustomerID(testCustomerUUID)
        equalsTestShoppingBasketDto(result)
    }

    fun addOfferingToShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.save(testShoppingBasket) } returns testShoppingBasket
        every { shoppingBasketItemRepository.save(testShoppingBasketItem) } returns testShoppingBasketItem
        val result = shoppingBasketService.addOfferingToShoppingBasket(testShoppingBasketUUID, testOfferingUUID, 13)
        equalsTestShoppingBasketDto(result!!)
    }

    fun removeItemFromShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.save(testShoppingBasket) } returns testShoppingBasket
        every { shoppingBasketItemRepository.save(testShoppingBasketItem) } returns testShoppingBasketItem
        val result = shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        equalsTestShoppingBasketDto(result!!)
    }

    fun removeItemFromShoppingBasketShouldThrowExceptionWhenShoppingBasketDoesNotExist() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.empty()
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        }
    }

    fun removeItemFromShoppingBasketShouldThrowExceptionWhenShoppingBasketIsEmpty() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        }
    }

    fun removeItemFromShoppingBasketShouldThrowExceptionWhenShoppingBasketItemDoesNotExist() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        every { shoppingBasketItemRepository.findById(testShoppingBasketItemUUID) } returns Optional.empty()
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        }
    }

    fun removeItemFromShoppingBasketShouldThrowExceptionWhenShoppingBasketItemIsNotInShoppingBasket() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        every { shoppingBasketItemRepository.findById(testShoppingBasketItemUUID) } returns Optional.of(testShoppingBasketItem)
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        }
    }

    fun modifyQuantityOfOfferingInShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.save(testShoppingBasket) } returns testShoppingBasket
        every { shoppingBasketItemRepository.save(testShoppingBasketItem) } returns testShoppingBasketItem
        val result = shoppingBasketService.modifyItemQuantity(testShoppingBasketUUID, testShoppingBasketItemUUID, 4)
        equalsTestShoppingBasketDto(result)
    }

    fun modifyQuantityOfOfferingInShoppingBasketShouldThrowExceptionWhenShoppingBasketDoesNotExist() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.empty()
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.modifyItemQuantity(testShoppingBasketUUID, testShoppingBasketItemUUID, 13)
        }
    }

    fun getShoppingBasketShouldReturnShoppingBasket() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        val result = shoppingBasketService.getShoppingBasket(testShoppingBasketUUID)
        equalsTestShoppingBasket(result)
    }

    //this is the same as the previous test, but for DTO. feel free to remove it if you think it's not needed (or is redundant)
    fun getShoppingBasketDTOShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        val result = shoppingBasketService.getShoppingBasketDTO(testShoppingBasketUUID)
        equalsTestShoppingBasketDto(result)
    }

    fun getShoppingBasketByCustomerIDShouldReturnShoppingBasket() {
        every { shoppingBasketRepository.findByCustomerID(testCustomerUUID) } returns testShoppingBasket
        val result = shoppingBasketService.getShoppingBasketByCustomerID(testCustomerUUID)
        equalsTestShoppingBasket(result)
    }

    fun getShoppingBasketDTOByCustomerIDShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.findByCustomerID(testCustomerUUID) } returns testShoppingBasket
        val result = shoppingBasketService.getShoppingBasketDTOByCustomerID(testCustomerUUID)
        equalsTestShoppingBasketDto(result)
    }

    fun getTheNumberOfItemsInShoppingBasketShouldReturnInt() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        val result = shoppingBasketService.numberOfItemsInShoppingBasket(testShoppingBasketUUID)
        Assertions.assertEquals(0, result)
    }

    fun getAListOfAllShoppingBasketsShouldReturnListOfShoppingBaskets() {
        every { shoppingBasketRepository.findAll() } returns listOf(testShoppingBasket)
        val result = shoppingBasketService.getAllShoppingBaskets()
        Assertions.assertEquals(listOf(testShoppingBasket), result)
    }

    fun deletingShoppingBasketShouldReturnTrue() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        val result = shoppingBasketService.deleteShoppingBasket(testShoppingBasketUUID)
        Assertions.assertEquals(true, result)
    }

    //TODO: this method doesn't really return anything, so i am not sure of the test here
    fun deletingAllShoppingBasketsShouldReturnUnit() {
        every { shoppingBasketRepository.findAll() } returns listOf(testShoppingBasket)
        val result = shoppingBasketService.deleteAllShoppingBaskets()
        Assertions.assertEquals(Unit, result)
    }

    fun updateShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.save(testShoppingBasket) } returns testShoppingBasket
        val result = shoppingBasketService.updateShoppingBasket(testShoppingBasket)
        equalsTestShoppingBasketDto(result)
    }

}