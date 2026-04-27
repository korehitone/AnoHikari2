package com.syntxr.anohikari3.data.repository

import com.syntxr.anohikari3.data.source.local.adzan.database.AdzanDao
import com.syntxr.anohikari3.data.source.remote.service.AdzanApi
import com.syntxr.anohikari3.domain.model.Adzan
import com.syntxr.anohikari3.domain.repository.AdzanRepository
import com.syntxr.anohikari3.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AdzanRepositoryImpl(
    private val api: AdzanApi,
    private val dao: AdzanDao,
) : AdzanRepository {
    override fun getAdzan(latitude: Double, longitude: Double): Flow<Resource<Adzan>> = flow {

        val cachedData = dao.getDataCache()

        try {
            val remoteAdzans = api.getAdzans(latitude.toString(), longitude.toString())
            if (remoteAdzans.times.isEmpty()) {
                throw IllegalStateException("Adzan times is empty")
            }
            val entity = remoteAdzans.toAdzanEntity()
            dao.upsertAll(entity)
            emit(Resource.Success(entity.toAdzan()))
        } catch (e: Exception) {
            emit(Resource.Error(data = cachedData?.toAdzan(), message = e.message ?: ""))
        }
    }

    override fun getCachedAdzan(): Flow<Resource<Adzan>> = flow {
        val cachedData = dao.getDataCache()

        if (cachedData != null) {
            emit(Resource.Success(cachedData.toAdzan()))
        } else {
            emit(Resource.Error(data = null, message = "No cached adzan data"))
        }
    }
}