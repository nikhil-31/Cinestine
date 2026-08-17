package nikhil.cinestine.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import nikhil.cinestine.model.MovieCategory
import nikhil.cinestine.ui.main.BrowseScrollHost
import nikhil.cinestine.ui.main.BrowseTitleHost
import nikhil.cinestine.ui.main.MediaTypeHost
import nikhil.cinestine.ui.main.MovieSelectionListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val category: MovieCategory by lazy {
        MovieCategory.valueOf(requireArguments().getString(ARG_CATEGORY)!!)
    }

    private val viewModel: MovieListViewModel by viewModels {
        MovieListViewModel.Factory(requireContext().cinestineApp.repository, category)
    }

    private val adapter = MovieAdapter(::onMovieSelected, ::onSaveClicked)

    val currentMediaType: MediaType get() = viewModel.currentMediaType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spanCount = resources.getInteger(R.integer.grid_span_count)
        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.recyclerMovies.layoutManager = layoutManager
        binding.recyclerMovies.adapter = adapter
        binding.mediaRow.isVisible = true
        binding.chipFilter.isVisible = true
        val showCatalog = requireArguments().getBoolean(ARG_SHOW_CATALOG, category == MovieCategory.POPULAR)
        binding.catalogScroll.isVisible = showCatalog
        val checkedId = if (viewModel.uiState.value.mediaType == MediaType.TV) {
            R.id.chip_tv
        } else {
            R.id.chip_movies
        }
        binding.categoryToggle.check(checkedId)
        binding.categoryToggle.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            viewModel.setMediaType(if (id == R.id.chip_tv) MediaType.TV else MediaType.MOVIE)
            applyCatalogLabels(viewModel.currentMediaType)
            notifyTitle()
            (activity as? MediaTypeHost)?.onBrowseMediaTypeChanged(viewModel.currentMediaType)
        }
        if (showCatalog) {
            binding.catalogToggle.check(catalogChipId(viewModel.currentCategory))
            binding.catalogToggle.setOnCheckedStateChangeListener { _, checkedIds ->
                val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
                viewModel.setCategory(catalogFromChip(id))
                notifyTitle()
            }
        }
        applyCatalogLabels(viewModel.currentMediaType)
        binding.chipFilter.isChecked = viewModel.currentFilter.isActive
        binding.chipFilter.setOnClickListener {
            binding.chipFilter.isChecked = viewModel.currentFilter.isActive
            DiscoverFilterFragment.newInstance(viewModel.currentMediaType, viewModel.currentFilter)
                .show(parentFragmentManager, "discover_filter")
        }
        parentFragmentManager.setFragmentResultListener(DiscoverFilterFragment.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            viewModel.setFilter(DiscoverFilterFragment.filterFrom(bundle))
            binding.chipFilter.isChecked = viewModel.currentFilter.isActive
            notifyTitle()
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
                            MovieListItem(movie, movie.favouriteKey in state.favouriteKeys)
                        }
                    )
                    binding.progress.isVisible = state.isLoading && state.movies.isEmpty()
                    binding.errorState.isVisible = state.error != null && state.movies.isEmpty()
                    binding.errorText.text = state.error
                    binding.chipFilter.isChecked = state.filter.isActive
                }
            }
        }
    }

    private fun applyCatalogLabels(mediaType: MediaType) {
        binding.chipCatalogNow.setText(
            if (mediaType == MediaType.TV) R.string.chip_airing_today else R.string.chip_now_playing
        )
        binding.chipCatalogUpcoming.setText(
            if (mediaType == MediaType.TV) R.string.chip_on_the_air else R.string.chip_upcoming
        )
    }

    private fun notifyTitle() {
        (activity as? BrowseTitleHost)?.onBrowseTitleChanged(browseTitle())
    }

    private fun browseTitle(): String {
        if (viewModel.currentFilter.isActive) return getString(R.string.action_filter)
        val tv = viewModel.currentMediaType == MediaType.TV
        return getString(
            when (viewModel.currentCategory) {
                MovieCategory.POPULAR -> R.string.title_popular
                MovieCategory.TOP_RATED -> R.string.title_top_rated
                MovieCategory.TRENDING -> R.string.title_trending
                MovieCategory.NOW_PLAYING -> if (tv) R.string.title_airing_today else R.string.title_now_playing
                MovieCategory.UPCOMING -> if (tv) R.string.title_on_the_air else R.string.title_upcoming
            }
        )
    }

    private fun catalogChipId(category: MovieCategory): Int = when (category) {
        MovieCategory.TRENDING -> R.id.chip_catalog_trending
        MovieCategory.NOW_PLAYING -> R.id.chip_catalog_now
        MovieCategory.UPCOMING -> R.id.chip_catalog_upcoming
        else -> R.id.chip_catalog_popular
    }

    private fun catalogFromChip(id: Int): MovieCategory = when (id) {
        R.id.chip_catalog_trending -> MovieCategory.TRENDING
        R.id.chip_catalog_now -> MovieCategory.NOW_PLAYING
        R.id.chip_catalog_upcoming -> MovieCategory.UPCOMING
        else -> MovieCategory.POPULAR
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

    companion object {
        private const val ARG_CATEGORY = "category"
        private const val ARG_SHOW_CATALOG = "show_catalog"

        fun newInstance(category: MovieCategory, showCatalog: Boolean = category == MovieCategory.POPULAR) =
            MovieListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category.name)
                    putBoolean(ARG_SHOW_CATALOG, showCatalog)
                }
            }
    }
}
