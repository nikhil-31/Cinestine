package nikhil.cinestine.ui.person

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
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.FragmentPersonBinding
import nikhil.cinestine.model.Movie
import nikhil.cinestine.ui.details.DetailsActivity
import nikhil.cinestine.ui.details.DetailsFragment
import nikhil.cinestine.ui.details.GalleryDialogFragment
import nikhil.cinestine.ui.details.StillAdapter
import nikhil.cinestine.ui.movie.MovieAdapter
import nikhil.cinestine.ui.movie.MovieListItem
import kotlinx.coroutines.launch

class PersonFragment : Fragment() {

    private var _binding: FragmentPersonBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PersonViewModel by viewModels {
        PersonViewModel.Factory(requireContext().cinestineApp.repository)
    }

    private val adapter = MovieAdapter(::onMovieSelected)
    private val stillAdapter = StillAdapter(::openGallery)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPersonBinding.inflate(inflater, container, false)
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
        binding.recyclerCredits.layoutManager = GridLayoutManager(
            requireContext(),
            resources.getInteger(R.integer.grid_span_count)
        )
        binding.recyclerCredits.adapter = adapter
        binding.recyclerPhotos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerPhotos.adapter = stillAdapter
        binding.creditToggle.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chip_credit_acting -> CreditFilter.ACTING
                R.id.chip_credit_directing -> CreditFilter.DIRECTING
                else -> CreditFilter.ALL
            }
            viewModel.setCreditFilter(filter)
        }
        val personId = requireActivity().intent.getStringExtra(EXTRA_PERSON_ID).orEmpty()
        val name = requireActivity().intent.getStringExtra(EXTRA_PERSON_NAME).orEmpty()
        if (name.isNotBlank()) binding.toolbar.title = name
        if (personId.isNotBlank()) viewModel.load(personId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: PersonUiState) {
        binding.progress.isVisible = state.isLoading && state.person == null
        binding.errorText.isVisible = state.error != null && state.person == null
        binding.errorText.text = state.error
        val person = state.person
        binding.personContent.isVisible = person != null
        if (person == null) return
        binding.toolbar.title = person.name
        binding.personName.text = person.name
        val meta = listOf(person.department, person.birthday, person.placeOfBirth)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
        binding.personMeta.isVisible = meta.isNotBlank()
        binding.personMeta.text = meta
        binding.personPhoto.load(person.profilePath.ifBlank { null }) {
            crossfade(true)
            transformations(CircleCropTransformation())
            placeholder(R.drawable.avatar_bg)
            error(R.drawable.avatar_bg)
        }
        binding.biographyCard.isVisible = person.biography.isNotBlank()
        binding.personBiography.text = person.biography
        binding.photosCard.isVisible = person.images.isNotEmpty()
        stillAdapter.submitList(person.images)
        val showCreditFilter = person.castCredits.isNotEmpty() && person.crewCredits.isNotEmpty()
        binding.creditToggle.isVisible = showCreditFilter
        if (showCreditFilter) {
            val checkedId = when (state.creditFilter) {
                CreditFilter.ACTING -> R.id.chip_credit_acting
                CreditFilter.DIRECTING -> R.id.chip_credit_directing
                CreditFilter.ALL -> R.id.chip_credit_all
            }
            if (binding.creditToggle.checkedChipId != checkedId) {
                binding.creditToggle.check(checkedId)
            }
        }
        val credits = when (state.creditFilter) {
            CreditFilter.ACTING -> person.castCredits
            CreditFilter.DIRECTING -> person.crewCredits
            CreditFilter.ALL -> person.credits
        }
        binding.knownForLabel.isVisible = credits.isNotEmpty()
        adapter.submitList(credits.map { MovieListItem(it) })
    }

    private fun openGallery(startIndex: Int) {
        val urls = viewModel.uiState.value.person?.images.orEmpty().map { it.url }.filter { it.isNotBlank() }
        if (urls.isEmpty()) return
        GalleryDialogFragment.newInstance(urls, startIndex)
            .show(parentFragmentManager, "gallery")
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
        const val EXTRA_PERSON_ID = "person_id"
        const val EXTRA_PERSON_NAME = "person_name"
    }
}
