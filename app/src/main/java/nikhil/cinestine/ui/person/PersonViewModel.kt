package nikhil.cinestine.ui.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import nikhil.cinestine.data.MovieRepository
import nikhil.cinestine.model.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

enum class CreditFilter { ALL, ACTING, DIRECTING }

data class PersonUiState(
    val person: Person? = null,
    val creditFilter: CreditFilter = CreditFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PersonViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val state = MutableStateFlow(PersonUiState(isLoading = true))
    val uiState: StateFlow<PersonUiState> = state.asStateFlow()

    fun setCreditFilter(filter: CreditFilter) {
        state.update { it.copy(creditFilter = filter) }
    }

    fun load(personId: String) {
        viewModelScope.launch {
            state.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.person(personId) }
                .onSuccess { person ->
                    state.update { it.copy(person = person, isLoading = false, error = null) }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    state.update {
                        it.copy(isLoading = false, error = error.message ?: "Unable to load person")
                    }
                }
        }
    }

    class Factory(
        private val repository: MovieRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PersonViewModel(repository) as T
        }
    }
}
