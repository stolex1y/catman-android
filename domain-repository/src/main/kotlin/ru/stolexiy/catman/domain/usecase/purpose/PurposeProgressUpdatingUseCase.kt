/*
package ru.stolexiy.catman.domain.usecase.purpose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import ru.stolexiy.catman.domain.repository.purpose.PurposeGettingRepository
import ru.stolexiy.common.di.CoroutineDispatcherNames
import javax.inject.Inject
import javax.inject.Named

class PurposeProgressUpdatingUseCase @Inject constructor(
    private val purposeGet: PurposeGettingRepository,
    private val purposeUpdate: PurposeUpdatingUseCase,
    @Named(CoroutineDispatcherNames.DEFAULT_DISPATCHER) private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(vararg purposeIds: Long): Result<Unit> {
        if (purposeIds.isEmpty())
            return Result.success(Unit)
        return runCatching {
            withContext(dispatcher) {
                purposeIds.map {
                    async {
                        purposeGet.byIdOnce(it)
                    }
                }.awaitAll().map {
                    it.getOrThrow() ?: throw IllegalArgumentException()
                }.run { purposeUpdate(*this.toTypedArray()) }
            }
        }
    }
}
*/
