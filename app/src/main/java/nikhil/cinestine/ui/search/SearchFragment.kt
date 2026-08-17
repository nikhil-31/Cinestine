package nikhil.cinestine.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import nikhil.cinestine.ui.main.MovieSelectionListener
import nikhil.cinestine.ui.main.SearchMediaTypeHost
import nikhil.cinestine.ui.movie.MovieAdapter
import nikhil.cinestine.ui.movie.MovieListItem
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels {
        SearchViewModel.Factory(requireContext().cinestineApp.repository)
    }

    private val adapter = MovieAdapter(::onMovieSelected, ::onSaveClicked)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spanCount = resources.getInteger(R.integer.grid_span_count)
        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.recyclerMovies.layoutManager = layoutManager
        binding.recyclerMovies.adapter = adapter
        binding.categoryToggle.isVisible = true
        applySearchChip(viewModel.uiState.value.mediaType)
        binding.categoryToggle.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val type = if (id == R.id.chip_tv) MediaType.TV else MediaType.MOVIE
            viewModel.setMediaType(type)
            (activity as? SearchMediaTypeHost)?.onSearchMediaTypeChanged(type)
        }
        binding.recyclerMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                (activity as? BrowseScrollHost)?.onBrowseListScrolled(dy)
                if (dy <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (lastVisible >= adapter.itemCount - 4) {
                    viewModel.loadNextPage()
                }
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
                    adapter.submitList(
                        state.movies.map { movie ->
                            MovieListItem(
                                movie,
                                movie.favouriteKey in state.favouriteKeys,
                                showTypeBadge = state.showTypeBadge
                            )
                        }
                    )
                    binding.progress.isVisible = state.isLoading && state.movies.isEmpty()
                    binding.errorState.isVisible = state.error != null && state.movies.isEmpty()
                    binding.errorText.text = state.error
                    binding.emptyState.isVisible = state.isIdle || state.isEmpty
                    applySearchChip(state.mediaType)
                    if (state.isIdle) {
                        binding.emptyText.setText(R.string.search_idle)
                        binding.emptyHint.setText(R.string.search_idle_hint)
                    } else {
                        binding.emptyText.setText(R.string.search_empty)
                        binding.emptyHint.setText(R.string.search_empty_hint)
                    }
                }
            }
        }
    }

    private fun applySearchChip(mediaType: MediaType?) {
        val checkedId = if (mediaType == MediaType.TV) R.id.chip_tv else R.id.chip_movies
        if (binding.categoryToggle.checkedChipId != checkedId) {
            binding.categoryToggle.check(checkedId)
        }
    }

    private fun onMovieSelected(movie: Movie) {
        (activity as? MovieSelectionListener)?.onMovieSelected(movie)
    }

    private fun onSaveClicked(movie: Movie) {
        val alreadySaved = viewModel.uiState.value.favouriteKeys.contains(movie.favouriteKey)
        viewModel.toggleFavourite(movie)
        Snackbar.make(
            binding.root,
            if (alreadySaved) R.string.movie_removed else R.string.movie_saved,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
