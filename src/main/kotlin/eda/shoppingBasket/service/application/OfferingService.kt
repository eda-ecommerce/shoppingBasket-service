package eda.shoppingBasket.service.application

import eda.shoppingBasket.service.application.exception.OfferingNotFoundException
import eda.shoppingBasket.service.model.OfferingMapper
import eda.shoppingBasket.service.model.dto.OfferingDTO
import eda.shoppingBasket.service.model.entity.Offering
import eda.shoppingBasket.service.repository.OfferingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class OfferingService {
    @Autowired
    lateinit var offeringRepository: OfferingRepository

    private val offeringMapper: OfferingMapper = OfferingMapper()

    fun saveOffering(offering: OfferingDTO){
        offeringRepository.save(offeringMapper.toEntity(offering))
    }

    fun deleteOffering(offeringId: UUID){
        offeringRepository.deleteById(offeringId)
    }

    fun getOffering(uuid: UUID): Offering {
        return offeringRepository.findByIdOrNull(uuid)?: throw OfferingNotFoundException()
    }

}