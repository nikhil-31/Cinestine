package nikhil.cinestine.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.Movie
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavouritesViewModel(
    repository: MovieRepository
) : ViewModel() {

    val favourites: StateFlow<List<Movie>> = repository.observeFavourites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    class Factory(
        private val repository: MovieRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavouritesViewModel(repository) as T
        }
    }
}
