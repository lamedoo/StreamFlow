package com.lukakordzaia.core.domain.usecases

import androidx.lifecycle.LiveData
import com.lukakordzaia.core.baseclasses.usecases.BaseUseCase
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.domain.repository.databaserepository.DatabaseRepository

class DbInsertContinueWatchingUseCase(
    private val repository: DatabaseRepository
) : BaseUseCase<ContinueWatchingRoom, Unit> {
    override suspend fun invoke(args: ContinueWatchingRoom?): Unit {
        return repository.insertContinueWatchingInRoom(args!!)
    }
}