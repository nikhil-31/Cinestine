package nikhil.cinestine.ui.search

import android.content.Intent
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
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.SearchScope
import nikhil.cinestine.ui.collection.CollectionActivity
import nikhil.cinestine.ui.collection.CollectionFragment
import nikhil.cinestine.ui.main.BrowseScrollHost
import nikhil.cinestine.ui.main.MovieSelectionListener
import nikhil.cinestine.ui.main.SearchMediaTypeHost
import nikhil.cinestine.ui.person.PersonActivity
import nikhil.cinestine.ui.person.PersonFragment
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels {
        SearchViewModel.Factory(requireContext().cinestineApp.repository)
    }

    private val adapter = SearchAdapter(::onMovieSelected, ::onPersonSelected, ::onCollectionSelected, ::onSaveClicked)

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
        binding.categoryToggle.isVisible = true
        binding.chipPeople.isVisible = true
        binding.chipCollections.isVisible = true
        applySearchChip(viewModel.uiState.value.scope)
        binding.categoryToggle.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val scope = when (id) {
                R.id.chip_tv -> SearchScope.TV
                R.id.chip_people -> SearchScope.PERSON
                R.id.chip_collections -> SearchScope.COLLECTION
                else -> SearchScope.MOVIE
            }
            viewModel.setScope(scope)
            (activity as? SearchMediaTypeHost)?.onSearchMediaTypeChanged(scope)
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
                    adapter.favouriteKeys = state.favouriteKeys
                    adapter.submitList(state.hits)
                    binding.progress.isVisible = state.isLoading && state.hits.isEmpty()
                    binding.errorState.isVisible = state.error != null && state.hits.isEmpty()
                    binding.errorText.text = state.error
                    binding.emptyState.isVisible = state.isIdle || state.isEmpty
                    applySearchChip(state.scope)
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

    private fun applySearchChip(scope: SearchScope) {
        val checkedId = when (scope) {
            SearchScope.TV -> R.id.chip_tv
            SearchScope.PERSON -> R.id.chip_people
            SearchScope.COLLECTION -> R.id.chip_collections
            SearchScope.MOVIE -> R.id.chip_movies
        }
        if (binding.categoryToggle.checkedChipId != checkedId) {
            binding.categoryToggle.check(checkedId)
        }
    }

    private fun onMovieSelected(movie: Movie) {
        (activity as? MovieSelectionListener)?.onMovieSelected(movie)
    }

    private fun onPersonSelected(id: String, name: String) {
        startActivity(
            Intent(requireContext(), PersonActivity::class.java)
                .putExtra(PersonFragment.EXTRA_PERSON_ID, id)
                .putExtra(PersonFragment.EXTRA_PERSON_NAME, name)
        )
    }

    private fun onCollectionSelected(id: String, name: String) {
        startActivity(
            Intent(requireContext(), CollectionActivity::class.java)
                .putExtra(CollectionFragment.EXTRA_COLLECTION_ID, id)
                .putExtra(CollectionFragment.EXTRA_COLLECTION_NAME, name)
        )
    }

    private fun onSaveClicked(movie: Movie) {
        viewModel.toggleFavourite(movie)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
