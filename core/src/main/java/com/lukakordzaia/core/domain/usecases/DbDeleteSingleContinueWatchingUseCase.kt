package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseUseCase
import com.lukakordzaia.core.domain.repository.databaserepository.DatabaseRepository

class DbDeleteSingleContinueWatchingUseCase(
    private val repository: DatabaseRepository
) : BaseUseCase<Int, Int> {
    override suspend fun invoke(args: Int?): Int {
        return repository.deleteSingleContinueWatchingFromRoom(args!!)
    }
}