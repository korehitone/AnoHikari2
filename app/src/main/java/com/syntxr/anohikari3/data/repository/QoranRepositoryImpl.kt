package com.syntxr.anohikari3.data.repository

import com.syntxr.anohikari3.data.source.local.qoran.database.QuranDao
import com.syntxr.anohikari3.data.source.local.qoran.entity.Jozz
import com.syntxr.anohikari3.data.source.local.qoran.entity.Qoran
import com.syntxr.anohikari3.data.source.local.qoran.entity.Sora
import com.syntxr.anohikari3.domain.repository.QoranRepository
import kotlinx.coroutines.flow.Flow

class QoranRepositoryImpl(
    private val dao: QuranDao
) : QoranRepository {
    override fun getSora(): Flow<List<Sora>> {
        return dao.getSora()
    }

    override fun getJozz(): Flow<List<Jozz>> {
        return dao.getJozz()
    }

    override fun getSoraAya(soraNo: Int): Flow<List<Qoran>> {
        return dao.getSoraAya(soraNo)
    }

    override fun getJozzAya(jozzNo: Int): Flow<List<Qoran>> {
        return dao.getJozzAya(jozzNo)
    }


    override fun searchAyaId(search: String): Flow<List<Qoran>> {
        return dao.searchAyaId(search)
    }

    override fun searchAyaEn(search: String): Flow<List<Qoran>> {
        return dao.searchAyaEn(search)
    }

    override fun searchSora(search: String): Flow<List<Qoran>> {
        return dao.searchSora(search)
    }
}