package eda.shoppingBasket.service.repository

import eda.shoppingBasket.service.entity.Offering
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OfferingRepository: CrudRepository<Offering, UUID> {

}