package com.syntxr.anohikari3.domain.repository

import com.syntxr.anohikari3.domain.model.Adzan
import com.syntxr.anohikari3.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AdzanRepository {
    fun getAdzan(latitude: Double, longitude: Double): Flow<Resource<Adzan>>
    fun getCachedAdzan(): Flow<Resource<Adzan>>
}