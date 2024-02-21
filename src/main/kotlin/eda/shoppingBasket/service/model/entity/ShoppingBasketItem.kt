package eda.shoppingBasket.service.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.UUID

@Entity
class ShoppingBasketItem (
    var quantity: Int = 0,
    @Id
    val shoppingBasketItemID: UUID = UUID.randomUUID(),
    val offeringID: UUID,
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE])
    @OnDelete(action = OnDeleteAction.CASCADE)
    val shoppingBasket: ShoppingBasket,
    var totalPrice: Float,
    var originalPrice: Float,
    var state: ItemState = ItemState.AVAILABLE
    )