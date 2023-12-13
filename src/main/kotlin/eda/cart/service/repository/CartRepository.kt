package eda.cart.service.repository

import eda.cart.service.entity.CartItem
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepository: CrudRepository<CartItem, Long>{

}