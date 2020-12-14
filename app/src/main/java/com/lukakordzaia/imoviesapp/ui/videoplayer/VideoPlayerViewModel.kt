package com.lukakordzaia.imoviesapp.ui.videoplayer

import com.lukakordzaia.imoviesapp.repository.MovieFilesRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel

class VideoPlayerViewModel : BaseViewModel() {
    private val repository = MovieFilesRepository()

//    private val _singleMovieFiles = MutableLiveData<MovieFiles.Data>()
//    val singleMovieFiles: LiveData<MovieFiles.Data> = _singleMovieFiles

//    fun getSingleMovieFiles(movieId: Int) {
//        viewModelScope.launch {
//            when (val files = repository.getSingleMovieFiles(movieId)) {
//                is Result.Success -> {
//                    _singleMovieFiles.value = files.data.data[0]
//                }
//                is Result.Error -> {
//                    Log.d("error", files.exception)
//                }
//            }
//        }
//    }
}