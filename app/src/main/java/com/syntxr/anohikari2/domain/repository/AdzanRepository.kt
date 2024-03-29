package com.syntxr.anohikari2.domain.repository

import com.syntxr.anohikari2.domain.model.Adzan
import com.syntxr.anohikari2.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AdzanRepository {
    fun getAdzan(latitude: Double, longitude: Double): Flow<Resource<Adzan>>
}