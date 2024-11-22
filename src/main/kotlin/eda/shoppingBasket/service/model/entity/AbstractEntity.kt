package eda.shoppingBasket.service.model.entity

import java.util.*

abstract class AbstractEntity(
    open val id: UUID = UUID.randomUUID()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}