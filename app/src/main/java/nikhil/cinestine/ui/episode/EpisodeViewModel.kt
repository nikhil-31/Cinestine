package nikhil.cinestine.ui.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.EpisodeDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class EpisodeUiState(
    val details: EpisodeDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class EpisodeViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val state = MutableStateFlow(EpisodeUiState(isLoading = true))
    val uiState: StateFlow<EpisodeUiState> = state.asStateFlow()

    fun load(tvId: String, seasonNumber: Int, episodeNumber: Int) {
        viewModelScope.launch {
            state.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.episodeDetails(tvId, seasonNumber, episodeNumber) }
                .onSuccess { details ->
                    state.value = EpisodeUiState(details = details)
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    state.update {
                        it.copy(isLoading = false, error = error.message ?: "Unable to load episode")
                    }
                }
        }
    }

    class Factory(
        private val repository: MovieRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EpisodeViewModel(repository) as T
        }
    }
}
