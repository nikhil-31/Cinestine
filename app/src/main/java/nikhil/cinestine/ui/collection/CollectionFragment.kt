package nikhil.cinestine.ui.collection

import android.content.Intent
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
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.FragmentCollectionBinding
import nikhil.cinestine.model.Movie
import nikhil.cinestine.ui.details.DetailsActivity
import nikhil.cinestine.ui.details.DetailsFragment
import nikhil.cinestine.ui.movie.MovieAdapter
import nikhil.cinestine.ui.movie.MovieListItem
import kotlinx.coroutines.launch

class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollectionViewModel by viewModels {
        CollectionViewModel.Factory(requireContext().cinestineApp.repository)
    }

    private val adapter = MovieAdapter(::onMovieSelected)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
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
        binding.recyclerParts.layoutManager = GridLayoutManager(
            requireContext(),
            resources.getInteger(R.integer.grid_span_count)
        )
        binding.recyclerParts.adapter = adapter
        val collectionId = requireActivity().intent.getStringExtra(EXTRA_COLLECTION_ID).orEmpty()
        val name = requireActivity().intent.getStringExtra(EXTRA_COLLECTION_NAME).orEmpty()
        if (name.isNotBlank()) binding.toolbar.title = name
        if (collectionId.isNotBlank()) viewModel.load(collectionId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: CollectionUiState) {
        binding.progress.isVisible = state.isLoading && state.collection == null
        binding.errorText.isVisible = state.error != null && state.collection == null
        binding.errorText.text = state.error
        val collection = state.collection
        binding.collectionContent.isVisible = collection != null
        if (collection == null) return
        binding.toolbar.title = collection.name
        binding.overviewCard.isVisible = collection.overview.isNotBlank()
        binding.collectionOverview.text = collection.overview
        binding.partsLabel.isVisible = collection.parts.isNotEmpty()
        if (collection.parts.isNotEmpty()) {
            binding.partsLabel.text = getString(R.string.collection_parts, collection.parts.size)
        }
        adapter.submitList(collection.parts.map { MovieListItem(it) })
    }

    private fun onMovieSelected(movie: Movie) {
        startActivity(
            Intent(requireContext(), DetailsActivity::class.java)
                .putExtra(DetailsFragment.EXTRA_MOVIE, movie)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_COLLECTION_ID = "collection_id"
        const val EXTRA_COLLECTION_NAME = "collection_name"
    }
}
