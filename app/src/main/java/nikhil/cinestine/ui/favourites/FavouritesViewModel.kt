package nikhil.cinestine.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FavouritesViewModel(
    repository: MovieRepository
) : ViewModel() {

    private val mediaType = MutableStateFlow(MediaType.MOVIE)

    val currentMediaType: MediaType get() = mediaType.value

    val favourites: StateFlow<List<Movie>> = combine(
        repository.observeFavourites(),
        mediaType
    ) { movies, type ->
        movies.filter { it.mediaType == type }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setMediaType(type: MediaType) {
        mediaType.value = type
    }

    class Factory(
        private val repository: MovieRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavouritesViewModel(repository) as T
        }
    }
}
