package nikhil.cinestine.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.CollectionSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class CollectionUiState(
    val collection: CollectionSummary? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CollectionViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val state = MutableStateFlow(CollectionUiState(isLoading = true))
    val uiState: StateFlow<CollectionUiState> = state.asStateFlow()

    fun load(collectionId: String) {
        viewModelScope.launch {
            state.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.collection(collectionId) }
                .onSuccess { collection ->
                    state.value = CollectionUiState(collection = collection)
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    state.update {
                        it.copy(isLoading = false, error = error.message ?: "Unable to load collection")
                    }
                }
        }
    }

    class Factory(
        private val repository: MovieRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CollectionViewModel(repository) as T
        }
    }
}
