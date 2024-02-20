package eda.shoppingBasket.service.model.dto

import eda.shoppingBasket.service.model.entity.ItemState
import java.util.UUID

data class ShoppingBasketItemDTO (
    val shoppingBasketItemId: UUID? = null,
    val shoppingBasketId: UUID,
    val offeringId: UUID,
    val quantity: Int,
    val totalPrice: Float,
    val itemState: ItemState
){
    override fun hashCode(): Int {
        var result = shoppingBasketItemId?.hashCode() ?: 0
        result = 31 * result + shoppingBasketId.hashCode()
        result = 31 * result + offeringId.hashCode()
        result = 31 * result + quantity
        result = 31 * result + totalPrice.hashCode()
        result = 31 * result + itemState.hashCode()
        return result
    }
    //TODO: Find solution to check ids in tests?
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoppingBasketItemDTO

        if (shoppingBasketId != other.shoppingBasketId) return false
        if (offeringId != other.offeringId) return false
        if (quantity != other.quantity) return false
        if (totalPrice != other.totalPrice) return false
        if (itemState != other.itemState) return false

        return true
    }
}