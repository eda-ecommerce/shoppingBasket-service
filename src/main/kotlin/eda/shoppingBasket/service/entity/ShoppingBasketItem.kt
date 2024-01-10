package eda.shoppingBasket.service.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
class ShoppingBasketItem (
    @ManyToOne
    var shoppingBasket: ShoppingBasket,
    var quantity: Int = 0,
    var itemPrice: Float,
    @Id
    val shoppingBasketItemID: UUID = UUID.randomUUID()
    )
{
}