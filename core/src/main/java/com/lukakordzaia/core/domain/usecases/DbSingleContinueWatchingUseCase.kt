package com.lukakordzaia.core.domain.usecases

import androidx.lifecycle.LiveData
import com.lukakordzaia.core.baseclasses.usecases.BaseUseCase
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.domain.repository.databaserepository.DatabaseRepository

class DbSingleContinueWatchingUseCase(
    private val repository: DatabaseRepository
) : BaseUseCase<Int, LiveData<ContinueWatchingRoom>> {
    override suspend fun invoke(args: Int?): LiveData<ContinueWatchingRoom> {
        return repository.getSingleContinueWatchingFromRoom(args!!)
    }
}