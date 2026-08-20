package nikhil.cinestine.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.FragmentDiscoverFilterBinding
import nikhil.cinestine.model.DiscoverFilter
import nikhil.cinestine.model.Genre
import nikhil.cinestine.model.MediaType
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class DiscoverFilterFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDiscoverFilterBinding? = null
    private val binding get() = _binding!!
    private var genres: List<Genre> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiscoverFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mediaType = MediaType.valueOf(requireArguments().getString(ARG_MEDIA_TYPE)!!)
        val selectedGenres = requireArguments().getIntArray(ARG_GENRES)?.toSet().orEmpty()
        val selectedYear = requireArguments().getInt(ARG_YEAR, NO_VALUE).takeIf { it != NO_VALUE }
        val selectedScore = requireArguments().getFloat(ARG_SCORE, NO_SCORE).takeIf { it != NO_SCORE }
        bindYears(selectedYear)
        bindScore(selectedScore)
        viewLifecycleOwner.lifecycleScope.launch {
            genres = requireContext().cinestineApp.repository.genres(mediaType)
            bindGenres(selectedGenres)
        }
        binding.clearButton.setOnClickListener { publish(DiscoverFilter()) }
        binding.applyButton.setOnClickListener { publish(currentFilter()) }
    }

    private fun bindGenres(selected: Set<Int>) {
        binding.genreGroup.removeAllViews()
        genres.forEach { genre ->
            binding.genreGroup.addView(
                Chip(requireContext(), null, com.google.android.material.R.attr.chipStyle).apply {
                    id = genre.id
                    text = genre.name
                    isCheckable = true
                    isChecked = genre.id in selected
                    setChipStyle()
                }
            )
        }
    }

    private fun bindYears(selectedYear: Int?) {
        val years = listOf(null) + (Calendar.getInstance().get(Calendar.YEAR) downTo 2018).toList()
        years.forEach { year ->
            binding.yearGroup.addView(
                Chip(requireContext(), null, com.google.android.material.R.attr.chipStyle).apply {
                    id = year ?: YEAR_ANY_ID
                    text = year?.toString() ?: getString(R.string.filter_any)
                    isCheckable = true
                    isChecked = year == selectedYear || (year == null && selectedYear == null)
                    setChipStyle()
                }
            )
        }
    }

    private fun bindScore(selectedScore: Float?) {
        binding.scoreGroup.check(
            when (selectedScore) {
                8f -> R.id.chip_score_8
                7f -> R.id.chip_score_7
                6f -> R.id.chip_score_6
                else -> R.id.chip_score_any
            }
        )
    }

    private fun Chip.setChipStyle() {
        setChipBackgroundColorResource(R.color.surface_variant)
        setTextColor(requireContext().getColor(R.color.on_surface))
        chipStrokeWidth = 0f
    }

    private fun currentFilter(): DiscoverFilter {
        val genreIds = binding.genreGroup.checkedChipIds.filter { it != View.NO_ID }.toSet()
        val yearId = binding.yearGroup.checkedChipId
        val year = yearId.takeIf { it != View.NO_ID && it != YEAR_ANY_ID }
        val score = when (binding.scoreGroup.checkedChipId) {
            R.id.chip_score_8 -> 8f
            R.id.chip_score_7 -> 7f
            R.id.chip_score_6 -> 6f
            else -> null
        }
        return DiscoverFilter(genreIds = genreIds, year = year, minScore = score)
    }

    private fun publish(filter: DiscoverFilter) {
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY,
            Bundle().apply {
                putIntArray(ARG_GENRES, filter.genreIds.toIntArray())
                putInt(ARG_YEAR, filter.year ?: NO_VALUE)
                putFloat(ARG_SCORE, filter.minScore ?: NO_SCORE)
            }
        )
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "discover_filter"
        const val ARG_GENRES = "genres"
        const val ARG_YEAR = "year"
        const val ARG_SCORE = "score"
        private const val ARG_MEDIA_TYPE = "media_type"
        private const val NO_VALUE = -1
        private const val NO_SCORE = -1f
        private const val YEAR_ANY_ID = 1

        fun newInstance(mediaType: MediaType, filter: DiscoverFilter) = DiscoverFilterFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_MEDIA_TYPE, mediaType.name)
                putIntArray(ARG_GENRES, filter.genreIds.toIntArray())
                putInt(ARG_YEAR, filter.year ?: NO_VALUE)
                putFloat(ARG_SCORE, filter.minScore ?: NO_SCORE)
            }
        }

        fun filterFrom(bundle: Bundle): DiscoverFilter {
            return DiscoverFilter(
                genreIds = bundle.getIntArray(ARG_GENRES)?.toSet().orEmpty(),
                year = bundle.getInt(ARG_YEAR, NO_VALUE).takeIf { it != NO_VALUE },
                minScore = bundle.getFloat(ARG_SCORE, NO_SCORE).takeIf { it != NO_SCORE }
            )
        }
    }
}
