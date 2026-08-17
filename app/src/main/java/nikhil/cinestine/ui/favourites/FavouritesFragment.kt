package nikhil.cinestine.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.FragmentMovieListBinding
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.ui.main.BrowseScrollHost
import nikhil.cinestine.ui.main.MediaTypeHost
import nikhil.cinestine.ui.main.MovieSelectionListener
import nikhil.cinestine.ui.movie.MovieAdapter
import nikhil.cinestine.ui.movie.MovieListItem
import kotlinx.coroutines.launch

class FavouritesFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouritesViewModel by viewModels {
        FavouritesViewModel.Factory(requireContext().cinestineApp.repository)
    }

    private val adapter = MovieAdapter(::onMovieSelected)

    val currentMediaType: MediaType get() = viewModel.currentMediaType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerMovies.layoutManager = GridLayoutManager(
            requireContext(),
            resources.getInteger(R.integer.grid_span_count)
        )
        binding.recyclerMovies.adapter = adapter
        binding.mediaRow.isVisible = true
        binding.categoryToggle.isVisible = true
        binding.savedSortScroll.isVisible = true
        binding.savedSearch.isVisible = true
        binding.swipeRefresh.isEnabled = false
        val checkedId = if (viewModel.currentMediaType == MediaType.TV) {
            R.id.chip_tv
        } else {
            R.id.chip_movies
        }
        binding.categoryToggle.check(checkedId)
        binding.categoryToggle.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            viewModel.setMediaType(if (id == R.id.chip_tv) MediaType.TV else MediaType.MOVIE)
            (activity as? MediaTypeHost)?.onBrowseMediaTypeChanged(viewModel.currentMediaType)
        }
        binding.savedSort.check(sortChipId(viewModel.uiState.value.sort))
        binding.savedSort.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            viewModel.setSort(sortFromChip(id))
        }
        binding.savedSearchInput.setText(viewModel.currentQuery)
        binding.savedSearchInput.doAfterTextChanged { text ->
            viewModel.setQuery(text?.toString().orEmpty())
        }
        binding.recyclerMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                (activity as? BrowseScrollHost)?.onBrowseListScrolled(dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    (activity as? BrowseScrollHost)?.onBrowseScrollSettled()
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.movies.map { MovieListItem(it, isFavourite = true) })
                    binding.emptyState.isVisible = state.movies.isEmpty()
                    if (state.searching) {
                        binding.emptyText.setText(R.string.empty_saved_search)
                        binding.emptyHint.setText(R.string.empty_saved_search_hint)
                    } else {
                        binding.emptyText.setText(
                            if (state.mediaType == MediaType.TV) {
                                R.string.empty_favourites_tv
                            } else {
                                R.string.empty_favourites_movies
                            }
                        )
                        binding.emptyHint.setText(R.string.empty_favourites_hint)
                    }
                }
            }
        }
    }

    private fun sortChipId(sort: SavedSort): Int = when (sort) {
        SavedSort.RECENT -> R.id.chip_sort_recent
        SavedSort.RATING -> R.id.chip_sort_rating
        SavedSort.TITLE -> R.id.chip_sort_title
    }

    private fun sortFromChip(id: Int): SavedSort = when (id) {
        R.id.chip_sort_rating -> SavedSort.RATING
        R.id.chip_sort_title -> SavedSort.TITLE
        else -> SavedSort.RECENT
    }

    private fun onMovieSelected(movie: Movie) {
        (activity as? MovieSelectionListener)?.onMovieSelected(movie)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
