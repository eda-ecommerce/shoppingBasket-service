package eda.cart.service

import eda.cart.service.dto.CartItemDTO
import eda.cart.service.entity.CartItem
import org.springframework.stereotype.Service

@Service
class CartApplicationService(){

    fun saveCart() {
        // TODO: (updates if necessary, and) saves the cart to the repository
 
    }

    fun getCart(): List<CartItem> {
        // TODO: fetches the cart from the repository

        return TODO("Provide the return value") //an incomplete return value here makes sure that "singleOrNull" works in addItem() and removeItem()
    }

    fun addItem(cartItem: CartItem) {
        val cart = getCart()

        val targetItem = cart.singleOrNull { it.productID == cartItem.productID }
        if (targetItem == null) {
            cartItem.quantity++
            // TODO: cart.add(cartItem) adds the item to the cart
        }
        else {
            targetItem.quantity++
        }
        saveCart()

    }

    fun removeItem(cartItem: CartItem) {
        val cart = getCart()

        val targetItem = cart.singleOrNull {it.productID == cartItem.productID }
        if (targetItem != null) {
            if (targetItem.quantity > 0) {
                targetItem.quantity--
            }
            else {
                // TODO: cart.remove(targetItem) removes the item from the cart
            }
        }
        saveCart()

    }

    fun getCartSize(): Int {
        var cartSize = 0
        getCart().forEach() {
            cartSize += it.quantity
        }

        return cartSize

    }


}