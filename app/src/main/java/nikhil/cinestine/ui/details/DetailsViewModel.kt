package nikhil.cinestine.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.Review
import nikhil.cinestine.model.Trailer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailsUiState(
    val movie: Movie? = null,
    val trailers: List<Trailer> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val isFavourite: Boolean = false
)

class DetailsViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val movie = MutableStateFlow<Movie?>(null)
    private val extras = MutableStateFlow(Extras())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DetailsUiState> = combine(
        movie,
        extras,
        movie.flatMapLatest { current ->
            if (current == null) flowOf(false) else repository.observeIsFavourite(current.id)
        }
    ) { currentMovie, extra, favourite ->
        DetailsUiState(
            movie = currentMovie,
            trailers = extra.trailers,
            reviews = extra.reviews,
            isFavourite = favourite
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailsUiState())

    fun show(movie: Movie) {
        this.movie.value = movie
        extras.value = Extras()
        viewModelScope.launch {
            val trailers = runCatching { repository.trailers(movie.id) }.getOrDefault(emptyList())
            val reviews = runCatching { repository.reviews(movie.id) }.getOrDefault(emptyList())
            extras.update { Extras(trailers, reviews) }
        }
    }

    fun toggleFavourite() {
        val current = movie.value ?: return
        viewModelScope.launch {
            repository.toggleFavourite(current, uiState.value.isFavourite)
        }
    }

    private data class Extras(
        val trailers: List<Trailer> = emptyList(),
        val reviews: List<Review> = emptyList()
    )

    class Factory(
        private val repository: MovieRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailsViewModel(repository) as T
        }
    }
}
