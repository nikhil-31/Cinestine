package nikhil.cinestine.ui.hot

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.FragmentHotBinding
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.DiscoverFilter
import nikhil.cinestine.ui.discover.DiscoverResultsActivity
import nikhil.cinestine.ui.discover.DiscoverResultsFragment
import nikhil.cinestine.ui.main.BrowseScrollHost
import nikhil.cinestine.ui.main.BrowseTitleHost
import nikhil.cinestine.ui.main.MediaTypeHost
import nikhil.cinestine.ui.main.MovieSelectionListener
import nikhil.cinestine.ui.movie.DiscoverFilterFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HotFragment : Fragment() {

    private var _binding: FragmentHotBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HotViewModel by viewModels {
        HotViewModel.Factory(requireContext().cinestineApp.repository)
    }

    private val adapter = HotRowAdapter(::onMovieSelected, ::onSaveClicked, ::onSeeAll, ::onNearEnd)
    private var skeletonAnimator: ObjectAnimator? = null

    val currentMediaType: MediaType get() = viewModel.currentMediaType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerRows.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRows.adapter = adapter
        applyScopeChip(viewModel.uiState.value.scope)
        binding.scopeToggle.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val scope = when (id) {
                R.id.chip_tv -> HotScope.TV
                R.id.chip_movies -> HotScope.MOVIE
                else -> HotScope.ALL
            }
            viewModel.setScope(scope)
            notifyTitle()
            (activity as? MediaTypeHost)?.onBrowseMediaTypeChanged(viewModel.currentMediaType)
        }
        binding.chipFilter.isChecked = viewModel.currentFilter.isActive
        binding.chipFilter.setOnClickListener {
            if (viewModel.uiState.value.scope == HotScope.ALL) {
                binding.chipFilter.isChecked = false
                Snackbar.make(binding.root, R.string.filter_all_disabled, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.chipFilter.isChecked = viewModel.currentFilter.isActive
            DiscoverFilterFragment.newInstance(viewModel.currentMediaType, viewModel.currentFilter)
                .show(childFragmentManager, "hot_filter")
        }
        binding.errorRetry.setOnClickListener { viewModel.refresh() }
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        childFragmentManager.setFragmentResultListener(
            DiscoverFilterFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setFilter(DiscoverFilterFragment.filterFrom(bundle))
            binding.chipFilter.isChecked = viewModel.currentFilter.isActive
            notifyTitle()
        }
        binding.recyclerRows.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                (activity as? BrowseScrollHost)?.onBrowseListScrolled(dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    (activity as? BrowseScrollHost)?.onBrowseScrollSettled()
                }
            }
        })
        notifyTitle()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.rows)
                    adapter.favouriteKeys = state.favouriteKeys
                    val showSkeleton = state.isLoading && state.rows.isEmpty()
                    setSkeletonVisible(showSkeleton)
                    binding.swipeRefresh.isRefreshing = state.isRefreshing
                    binding.errorState.isVisible = state.error != null && state.rows.isEmpty()
                    binding.errorText.text = state.error
                    binding.emptyState.isVisible = !state.isLoading && state.error == null && state.rows.isEmpty()
                    val filtersEnabled = state.scope != HotScope.ALL
                    binding.chipFilter.isEnabled = filtersEnabled
                    binding.chipFilter.alpha = if (filtersEnabled) 1f else 0.4f
                    binding.chipFilter.isChecked = filtersEnabled && state.filter.isActive
                    applyScopeChip(state.scope)
                }
            }
        }
    }

    private fun applyScopeChip(scope: HotScope) {
        val checkedId = when (scope) {
            HotScope.TV -> R.id.chip_tv
            HotScope.MOVIE -> R.id.chip_movies
            HotScope.ALL -> R.id.chip_all
        }
        if (binding.scopeToggle.checkedChipId != checkedId) {
            binding.scopeToggle.check(checkedId)
        }
    }

    private fun notifyTitle() {
        val filtered = viewModel.uiState.value.scope != HotScope.ALL && viewModel.currentFilter.isActive
        (activity as? BrowseTitleHost)?.onBrowseTitleChanged(
            getString(if (filtered) R.string.action_filter else R.string.title_popular)
        )
    }

    private fun onMovieSelected(movie: Movie) {
        (activity as? MovieSelectionListener)?.onMovieSelected(movie)
    }

    private fun onSaveClicked(movie: Movie) {
        viewModel.toggleFavourite(movie)
    }

    private fun onSeeAll(row: HotRow) {
        if (!row.showSeeAll) return
        val filter = if (viewModel.uiState.value.scope == HotScope.ALL) {
            DiscoverFilter()
        } else {
            viewModel.currentFilter
        }
        startActivity(
            Intent(requireContext(), DiscoverResultsActivity::class.java)
                .putExtra(DiscoverResultsFragment.EXTRA_TITLE, getString(row.titleRes))
                .putExtra(DiscoverResultsFragment.EXTRA_MEDIA_TYPE, row.mediaType.name)
                .putExtra(DiscoverResultsFragment.EXTRA_FILTER, filter)
                .putExtra(DiscoverResultsFragment.EXTRA_CATEGORY, row.category.name)
        )
    }

    private fun onNearEnd(row: HotRow) {
        viewModel.loadNextPage(row.key)
    }

    private fun setSkeletonVisible(visible: Boolean) {
        val skeleton = binding.skeleton.root
        skeleton.isVisible = visible
        if (visible) {
            if (skeletonAnimator?.isRunning == true) return
            skeletonAnimator = ObjectAnimator.ofFloat(skeleton, View.ALPHA, 1f, 0.45f).apply {
                duration = 800
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                start()
            }
        } else {
            skeletonAnimator?.cancel()
            skeletonAnimator = null
            skeleton.alpha = 1f
        }
    }

    override fun onDestroyView() {
        skeletonAnimator?.cancel()
        skeletonAnimator = null
        super.onDestroyView()
        _binding = null
    }
}
