package eda.shoppingBasket.service

import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketDTO
import eda.shoppingBasket.service.model.dto.ShoppingBasketItemDTO
import eda.shoppingBasket.service.model.entity.ItemState
import eda.shoppingBasket.service.model.entity.Offering
import eda.shoppingBasket.service.model.entity.ShoppingBasket
import eda.shoppingBasket.service.model.entity.ShoppingBasketItem
import java.util.*

val customerID: UUID = UUID.randomUUID()
val basketID: UUID = UUID.randomUUID()
val itemID1: UUID = UUID.randomUUID()
val itemID2: UUID = UUID.randomUUID()
val offeringID1: UUID = UUID.randomUUID()
val offeringID2: UUID = UUID.randomUUID()

val testOffering1 = Offering(1,1.0, Offering.Status.ACTIVE, offeringID1)
val testOffering2 = Offering(2,2.0, Offering.Status.ACTIVE, offeringID2)
val testOffering1DTO = OfferingDTO(offeringID1, null, 1, 1.0, Offering.Status.ACTIVE)
val testOffering2DTO = OfferingDTO(offeringID2, null, 2, 2.0, Offering.Status.ACTIVE)

val testItem1 = ShoppingBasketItem(1, 1.0, itemID1, offeringID1, 1.0, ItemState.AVAILABLE)
val testItem1Unavailable = ShoppingBasketItem(1, 1.0, itemID1, offeringID1, 1.0, ItemState.UNAVAILABLE)
val testItem2 = ShoppingBasketItem(2, 4.0, itemID2, offeringID2, 2.0, ItemState.AVAILABLE)
val testItem1DTO = ShoppingBasketItemDTO(offeringID1,1,1.0,1.0, ItemState.AVAILABLE, itemID1)
val testItem2DTO = ShoppingBasketItemDTO(offeringID2,2,4.0,2.0, ItemState.AVAILABLE, itemID2)

val testBasketFull = ShoppingBasket(customerID, 5.0, 2, basketID, mutableListOf(testItem1, testItem2))
val testBasketFullNotReady = ShoppingBasket(customerID, 5.0, 2, basketID, mutableListOf(testItem1Unavailable, testItem2))

val testBasketEmpty = ShoppingBasket(customerID, 0.0, 0, basketID, mutableListOf())
val testBasketDTOFull = ShoppingBasketDTO(customerID, 5.0, 2, basketID, mutableListOf(testItem1DTO, testItem2DTO))
val testBasketDTOEmpty = ShoppingBasketDTO(customerID, 0.0, 0, basketID, mutableListOf())