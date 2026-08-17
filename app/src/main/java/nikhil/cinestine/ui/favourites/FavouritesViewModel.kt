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

enum class SavedSort { RECENT, RATING, TITLE }

data class FavouritesUiState(
    val movies: List<Movie> = emptyList(),
    val mediaType: MediaType = MediaType.MOVIE,
    val sort: SavedSort = SavedSort.RECENT,
    val query: String = "",
    val searching: Boolean = false
)

class FavouritesViewModel(
    repository: MovieRepository
) : ViewModel() {

    private val mediaType = MutableStateFlow(MediaType.MOVIE)
    private val sort = MutableStateFlow(SavedSort.RECENT)
    private val query = MutableStateFlow("")

    val currentMediaType: MediaType get() = mediaType.value
    val currentQuery: String get() = query.value

    val uiState: StateFlow<FavouritesUiState> = combine(
        repository.observeFavourites(),
        mediaType,
        sort,
        query
    ) { movies, type, sortOrder, search ->
        val filtered = movies
            .filter { it.mediaType == type }
            .filter { movie ->
                search.isBlank() ||
                    movie.title.contains(search, ignoreCase = true) ||
                    movie.originalTitle.contains(search, ignoreCase = true)
            }
            .sortedWith(
                when (sortOrder) {
                    SavedSort.RECENT -> compareByDescending(Movie::savedAt)
                    SavedSort.RATING -> compareByDescending(Movie::voteAverage)
                    SavedSort.TITLE -> compareBy { it.title.lowercase() }
                }
            )
        FavouritesUiState(
            movies = filtered,
            mediaType = type,
            sort = sortOrder,
            query = search,
            searching = search.isNotBlank()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavouritesUiState())

    fun setMediaType(type: MediaType) {
        mediaType.value = type
    }

    fun setSort(sort: SavedSort) {
        this.sort.value = sort
    }

    fun setQuery(query: String) {
        this.query.value = query
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
