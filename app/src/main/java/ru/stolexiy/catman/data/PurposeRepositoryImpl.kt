package ru.stolexiy.catman.data

import kotlinx.coroutines.flow.Flow
import ru.stolexiy.catman.domain.model.Purpose
import ru.stolexiy.catman.domain.repository.PurposeRepository

class PurposeRepositoryImpl : PurposeRepository {
    override fun getPurpose(id: Long): Flow<Purpose> {
        TODO("Not yet implemented")
    }

    override fun getAllPurposes(): Flow<List<Purpose>> {
        TODO("Not yet implemented")
    }

    override fun updatePurpose(purpose: Purpose): Flow<Purpose> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePurpose(purpose: Purpose) {
        TODO("Not yet implemented")
    }
}