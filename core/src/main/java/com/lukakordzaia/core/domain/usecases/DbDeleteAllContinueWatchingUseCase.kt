package com.lukakordzaia.core.domain.usecases

import com.lukakordzaia.core.baseclasses.usecases.BaseUseCase
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.core.network.ResultDomain

class DbDeleteAllContinueWatchingUseCase(
    private val repository: DatabaseRepository,
) : BaseUseCase<Unit, Unit> {
    override suspend fun invoke(args: Unit?) {
        return repository.deleteAllFromRoom()
    }
}