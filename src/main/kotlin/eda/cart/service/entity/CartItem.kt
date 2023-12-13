package eda.cart.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class CartItem (
    var productID: Long,
    var quantity: Int = 0,
    var unitPrice: Double,
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // TODO: should the generationType be auto?
    var cartID: Long
        ) {
}