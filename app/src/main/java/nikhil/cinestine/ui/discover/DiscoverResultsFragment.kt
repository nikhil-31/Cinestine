package nikhil.cinestine.ui.discover

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.FragmentDiscoverResultsBinding
import nikhil.cinestine.model.DiscoverFilter
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.MovieCategory
import nikhil.cinestine.ui.main.MovieSelectionListener
import nikhil.cinestine.ui.movie.MovieAdapter
import nikhil.cinestine.ui.movie.MovieListItem
import kotlinx.coroutines.launch

class DiscoverResultsFragment : Fragment() {

    private var _binding: FragmentDiscoverResultsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiscoverResultsViewModel by viewModels {
        val intent = requireActivity().intent
        DiscoverResultsViewModel.Factory(
            requireContext().cinestineApp.repository,
            mediaTypeFrom(intent),
            filterFrom(intent),
            categoryFrom(intent)
        )
    }

    private val adapter = MovieAdapter(::onMovieSelected, ::onSaveClicked)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiscoverResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { toolbar, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            toolbar.updatePadding(top = bars.top)
            insets
        }
        val title = requireActivity().intent.getStringExtra(EXTRA_TITLE).orEmpty()
        if (title.isNotBlank()) binding.toolbar.title = title

        val list = binding.list
        val spanCount = resources.getInteger(R.integer.grid_span_count)
        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        list.recyclerMovies.layoutManager = layoutManager
        list.recyclerMovies.adapter = adapter
        list.mediaRow.isVisible = false
        list.catalogScroll.isVisible = false
        list.errorRetry.setOnClickListener { viewModel.refresh() }
        list.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        list.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        list.recyclerMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (lastVisible >= adapter.itemCount - 4) {
                    viewModel.loadNextPage()
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(
                        state.movies.map { movie ->
                            MovieListItem(movie, movie.favouriteKey in state.favouriteKeys)
                        }
                    )
                    list.progress.isVisible = state.isLoading && state.movies.isEmpty()
                    list.swipeRefresh.isRefreshing = state.isRefreshing
                    list.errorState.isVisible = state.error != null && state.movies.isEmpty()
                    list.errorText.text = state.error
                    list.emptyState.isVisible = !state.isLoading && state.error == null && state.movies.isEmpty()
                    list.emptyText.setText(R.string.discover_empty)
                    list.emptyHint.setText(R.string.discover_empty_hint)
                }
            }
        }
    }

    private fun onMovieSelected(movie: Movie) {
        (activity as? MovieSelectionListener)?.onMovieSelected(movie)
    }

    private fun onSaveClicked(movie: Movie) {
        viewModel.toggleFavourite(movie)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_TITLE = "discover_title"
        const val EXTRA_MEDIA_TYPE = "discover_media_type"
        const val EXTRA_FILTER = "discover_filter"
        const val EXTRA_CATEGORY = "discover_category"

        fun mediaTypeFrom(intent: Intent): MediaType {
            val raw = intent.getStringExtra(EXTRA_MEDIA_TYPE) ?: return MediaType.MOVIE
            return runCatching { MediaType.valueOf(raw) }.getOrDefault(MediaType.MOVIE)
        }

        fun categoryFrom(intent: Intent): MovieCategory? {
            val raw = intent.getStringExtra(EXTRA_CATEGORY) ?: return null
            return runCatching { MovieCategory.valueOf(raw) }.getOrNull()
        }

        fun filterFrom(intent: Intent): DiscoverFilter {
            return if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(EXTRA_FILTER, DiscoverFilter::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_FILTER)
            } ?: DiscoverFilter()
        }
    }
}
