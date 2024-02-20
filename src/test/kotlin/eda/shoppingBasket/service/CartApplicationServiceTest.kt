package eda.shoppingBasket.service

import com.ninjasquad.springmockk.MockkBean
import eda.shoppingBasket.service.application.ShoppingBasketService
import eda.shoppingBasket.service.eventing.ShoppingBasketProducer
import eda.shoppingBasket.service.model.ShoppingBasketMapper
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketItemDTO
import eda.shoppingBasket.service.model.entity.Offering
import eda.shoppingBasket.service.model.entity.ShoppingBasket
import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import eda.shoppingBasket.service.repository.OfferingRepository
import eda.shoppingBasket.service.repository.ShoppingBasketItemRepository
import eda.shoppingBasket.service.repository.ShoppingBasketRepository
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
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

    @MockkBean
    lateinit var shoppingBasketProducer: ShoppingBasketProducer

    @Autowired
    lateinit var shoppingBasketService: ShoppingBasketService

    final val shoppingBasketMapper: ShoppingBasketMapper = ShoppingBasketMapper()

    private val testShoppingBasketItemUUID = randomUUID()
    private val testShoppingBasketUUID = randomUUID()
    private val testCustomerUUID = randomUUID()
    private val testOfferingUUID = randomUUID()

    @BeforeEach
    fun setup() {
        every { shoppingBasketRepository.findByIdOrNull(testShoppingBasketUUID) } returns testShoppingBasket
        every { shoppingBasketItemRepository.findByIdOrNull(testShoppingBasketItemUUID) } returns testShoppingBasketItem
        every { offeringRepository.findByIdOrNull(testOfferingUUID) } returns testOffering
        every { shoppingBasketRepository.findByShoppingBasketID(any()) } returns testShoppingBasket
        every { shoppingBasketProducer.sendMessage(any(),any()) } returns Unit
    }

    final val testShoppingBasket = ShoppingBasket(
        shoppingBasketID = testShoppingBasketUUID,
        customerID = testCustomerUUID,
        totalPrice = 0.0f
    )

    val emptyTestShoppingBasketDTO = shoppingBasketMapper.toDTO(testShoppingBasket, mutableListOf())

    final val testOffering = Offering(
        offeringID = testOfferingUUID,
        quantity = 1,
        price = 5.6f
    )

    final val testShoppingBasketItem = ShoppingBasketItem(
        shoppingBasketItemID = testShoppingBasketItemUUID,
        shoppingBasket = testShoppingBasket,
        quantity = 3,
        totalPrice = 4.2f,
        offeringID = testOffering.offeringID,
        originalPrice = testOffering.price,
    )

    final val fullTestShoppingBasket = ShoppingBasket(
        shoppingBasketID = testShoppingBasketUUID,
        customerID = testCustomerUUID,
        totalPrice = 4.2f,
    )

    final val testShoppingBasketItemDto = ShoppingBasketItemDTO(
        shoppingBasketId = testShoppingBasketItem.shoppingBasket.shoppingBasketID,
        offeringId = testShoppingBasketItem.offeringID,
        totalPrice = testShoppingBasketItem.totalPrice,
        itemState = testShoppingBasketItem.state,
        quantity = testShoppingBasketItem.quantity,
        shoppingBasketItemId = testShoppingBasketItem.shoppingBasketItemID
    )

    final val testShoppingBasketDto = ShoppingBasketDTO(
        shoppingBasketId = testShoppingBasket.shoppingBasketID,
        customerId = testShoppingBasket.customerID,
        totalPrice = testShoppingBasketItem.totalPrice,
        totalItemQuantity = 1,
        items = mutableListOf(testShoppingBasketItemDto)
    )

    fun equalsTestShoppingBasketDto(givenDTO: ShoppingBasketDTO, compareTo: ShoppingBasket) {
        Assertions.assertEquals(givenDTO.shoppingBasketId, emptyTestShoppingBasketDTO.shoppingBasketId)
        Assertions.assertEquals(givenDTO.customerId, emptyTestShoppingBasketDTO.customerId)
        Assertions.assertEquals(givenDTO.totalPrice, emptyTestShoppingBasketDTO.totalPrice)
        Assertions.assertEquals(givenDTO.totalItemQuantity,emptyTestShoppingBasketDTO.totalItemQuantity)
    }

    fun equalsTestShoppingBasketDto(givenDTO: ShoppingBasketDTO, compareTo: ShoppingBasketDTO) {
        assert(givenDTO.shoppingBasketId == compareTo.shoppingBasketId)
        assert(givenDTO.customerId == compareTo.customerId)
        assert(givenDTO.totalPrice == compareTo.totalPrice)
        assert(givenDTO.totalItemQuantity == compareTo.totalItemQuantity)
        assert(givenDTO.items == compareTo.items)
    }

    fun equalsTestShoppingBasket(givenShoppingBasket: ShoppingBasket) {
        Assertions.assertEquals(givenShoppingBasket.shoppingBasketID , testShoppingBasket.shoppingBasketID)
        Assertions.assertEquals(givenShoppingBasket.customerID , testShoppingBasket.customerID)
        Assertions.assertEquals(givenShoppingBasket.totalPrice , testShoppingBasket.totalPrice)
    }

    @Test
    fun createShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketItemRepository.findByShoppingBasket(testShoppingBasket) } returns listOf()
        every { shoppingBasketRepository.save(any()) } returns testShoppingBasket
        every { shoppingBasketRepository.findByShoppingBasketID(any()) } returns null //not present
        every { shoppingBasketRepository.findByCustomerID(any()) } returns null //not present
        val result = shoppingBasketService.createShoppingBasket(emptyTestShoppingBasketDTO)
        equalsTestShoppingBasketDto(result, testShoppingBasket)
    }

    @Test
    fun addOfferingToShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketItemRepository.findByShoppingBasket(testShoppingBasket) } returns listOf(testShoppingBasketItem)
        every { shoppingBasketRepository.save(testShoppingBasket) } returns testShoppingBasket
        every { shoppingBasketItemRepository.save(any()) } returns testShoppingBasketItem
        every { shoppingBasketItemRepository.findByShoppingBasketAndOfferingID(any(),any()) } returns null //Not present
        val result = shoppingBasketService.addOfferingToShoppingBasket(testShoppingBasketUUID, testOfferingUUID, 13)
        equalsTestShoppingBasketDto(result!!, testShoppingBasketDto)
    }
    //@Test
    //disabled because we need to split this into two tests, one for base SB and one for items
    fun removeItemFromShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.findByIdOrNull(testShoppingBasketUUID) } returns fullTestShoppingBasket
        every { shoppingBasketItemRepository.findByShoppingBasketAndShoppingBasketItemID(fullTestShoppingBasket, testShoppingBasketItemUUID) } returns testShoppingBasketItem
        every { shoppingBasketItemRepository.delete(testShoppingBasketItem) } returns Unit
        val result = shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        equalsTestShoppingBasketDto(result!!, testShoppingBasket)
    }
    @Test
    fun removeItemFromShoppingBasketShouldThrowExceptionWhenShoppingBasketDoesNotExist() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.empty()
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        }
    }
    @Test
    fun removeItemFromShoppingBasketShouldThrowExceptionWhenShoppingBasketIsEmpty() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        }
    }
    @Test
    fun removeItemFromShoppingBasketShouldThrowExceptionWhenShoppingBasketItemDoesNotExist() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        every { shoppingBasketItemRepository.findById(testShoppingBasketItemUUID) } returns Optional.empty()
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        }
    }
    @Test
    fun removeItemFromShoppingBasketShouldThrowExceptionWhenShoppingBasketItemIsNotInShoppingBasket() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        every { shoppingBasketItemRepository.findById(testShoppingBasketItemUUID) } returns Optional.of(testShoppingBasketItem)
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.removeItemFromShoppingBasket(testShoppingBasketUUID, testShoppingBasketItemUUID)
        }
    }
    //@Test
    fun modifyQuantityOfOfferingInShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.save(testShoppingBasket) } returns testShoppingBasket
        every { shoppingBasketItemRepository.save(testShoppingBasketItem) } returns testShoppingBasketItem
        val result = shoppingBasketService.modifyItemQuantity(testShoppingBasketUUID, testShoppingBasketItemUUID, 4)
        equalsTestShoppingBasketDto(result, testShoppingBasket)
    }
    @Test
    fun modifyQuantityOfOfferingInShoppingBasketShouldThrowExceptionWhenShoppingBasketDoesNotExist() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.empty()
        Assertions.assertThrows(Exception::class.java) {
            shoppingBasketService.modifyItemQuantity(testShoppingBasketUUID, testShoppingBasketItemUUID, 13)
        }
    }
    @Test
    fun getShoppingBasketShouldReturnShoppingBasket() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        val result = shoppingBasketService.getShoppingBasket(testShoppingBasketUUID)
        equalsTestShoppingBasket(result)
    }
    //@Test
    //this is the same as the previous test, but for DTO. feel free to remove it if you think it's not needed (or is redundant)
    fun getShoppingBasketDTOShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        val result = shoppingBasketService.getShoppingBasketDTO(testShoppingBasketUUID)
        equalsTestShoppingBasketDto(result, testShoppingBasket)
    }
    @Test
    fun getShoppingBasketByCustomerIDShouldReturnShoppingBasket() {
        every { shoppingBasketRepository.findByCustomerID(testCustomerUUID) } returns testShoppingBasket
        val result = shoppingBasketService.getShoppingBasketByCustomerID(testCustomerUUID)
        equalsTestShoppingBasket(result)
    }
    //@Test
    fun getShoppingBasketDTOByCustomerIDShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.findByCustomerID(testCustomerUUID) } returns testShoppingBasket
        val result = shoppingBasketService.getShoppingBasketDTOByCustomerID(testCustomerUUID)
        equalsTestShoppingBasketDto(result, testShoppingBasket)
    }
    //@Test
    fun getTheNumberOfItemsInShoppingBasketShouldReturnInt() {
        every { shoppingBasketRepository.findById(testShoppingBasketUUID) } returns Optional.of(testShoppingBasket)
        val result = shoppingBasketService.numberOfItemsInShoppingBasket(testShoppingBasketUUID)
        Assertions.assertEquals(0, result)
    }
    @Test
    fun getAListOfAllShoppingBasketsShouldReturnListOfShoppingBaskets() {
        every { shoppingBasketRepository.findAll() } returns listOf(testShoppingBasket)
        every { shoppingBasketItemRepository.findByShoppingBasket(testShoppingBasket) } returns listOf(testShoppingBasketItem)
        val result = shoppingBasketService.getAllShoppingBaskets()
        Assertions.assertEquals(listOf(testShoppingBasketDto), result)
    }
    //@Test
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
    //@Test
    fun updateShoppingBasketShouldReturnShoppingBasketDTO() {
        every { shoppingBasketRepository.save(testShoppingBasket) } returns testShoppingBasket
        val result = shoppingBasketService.updateShoppingBasket(testShoppingBasket)
        equalsTestShoppingBasketDto(result, testShoppingBasket)
    }

}