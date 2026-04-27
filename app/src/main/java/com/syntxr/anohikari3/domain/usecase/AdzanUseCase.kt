package com.syntxr.anohikari3.domain.usecase

import com.syntxr.anohikari3.domain.model.Adzan
import com.syntxr.anohikari3.domain.repository.AdzanRepository
import com.syntxr.anohikari3.utils.Resource
import kotlinx.coroutines.flow.Flow

class AdzanUseCase(
    private val repository: AdzanRepository
) {
    fun getAdzans(latitude: Double, longitude: Double) : Flow<Resource<Adzan>> =
        repository.getAdzan(latitude, longitude)

    fun getCachedAdzan(): Flow<Resource<Adzan>> = repository.getCachedAdzan()
}