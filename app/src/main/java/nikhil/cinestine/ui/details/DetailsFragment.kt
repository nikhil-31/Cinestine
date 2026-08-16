package nikhil.cinestine.ui.details

import android.content.Intent
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.snackbar.Snackbar
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.FragmentDetailsBinding
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.Trailer
import kotlinx.coroutines.launch
import java.util.Locale

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModels {
        DetailsViewModel.Factory(requireContext().cinestineApp.repository)
    }

    private val trailerAdapter = TrailerAdapter()
    private val reviewAdapter = ReviewAdapter()
    private var overviewExpanded = false
    private var overviewText = ""
    private var renderedMovieId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (requireActivity() is DetailsActivity) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            binding.toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        binding.toolbar.inflateMenu(R.menu.menu_details)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_share) {
                shareFirstTrailer()
                true
            } else {
                false
            }
        }

        binding.recyclerTrailer.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerTrailer.adapter = trailerAdapter
        binding.recyclerReview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerReview.adapter = reviewAdapter
        binding.recyclerReview.isNestedScrollingEnabled = false

        ViewCompat.setOnApplyWindowInsetsListener(binding.detailsScroll) { scroll, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            scroll.updatePadding(bottom = bars.bottom + resources.getDimensionPixelSize(R.dimen.fab_margin))
            insets
        }

        binding.fab.setOnClickListener {
            val currentlyFavourite = viewModel.uiState.value.isFavourite
            viewModel.toggleFavourite()
            Snackbar.make(
                binding.root,
                if (currentlyFavourite) R.string.movie_removed else R.string.movie_saved,
                Snackbar.LENGTH_SHORT
            ).show()
        }
        binding.playTrailer.setOnClickListener {
            viewModel.uiState.value.trailers.firstOrNull()?.let(::openTrailer)
        }
        binding.overviewToggle.setOnClickListener {
            overviewExpanded = !overviewExpanded
            applyOverview()
        }

        movieFromIntent()?.let(viewModel::show)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    fun showMovie(movie: Movie) {
        viewModel.show(movie)
    }

    private fun render(state: DetailsUiState) {
        val movie = state.movie
        if (movie == null) {
            binding.titleWrite.setText(R.string.Select)
            binding.overviewNew.setText(R.string.empty_details)
            binding.overviewToggle.isVisible = false
            binding.posterCard.isVisible = false
            binding.metaPills.isVisible = false
            binding.releaseWrite.isVisible = false
            binding.metaLanguage.isVisible = false
            binding.metaVotes.isVisible = false
            binding.scoreCluster.isVisible = false
            binding.playTrailer.isVisible = false
            binding.trailersCard.isVisible = false
            binding.reviewsCard.isVisible = false
            binding.fab.isVisible = false
            binding.toolbar.menu.findItem(R.id.action_share)?.isVisible = false
            return
        }

        val movieChanged = movie.id != renderedMovieId
        if (movieChanged) {
            renderedMovieId = movie.id
            overviewExpanded = false
        }

        binding.posterCard.isVisible = true
        binding.metaPills.isVisible = true
        binding.releaseWrite.isVisible = true
        binding.metaLanguage.isVisible = movie.originalLanguage.isNotBlank()
        binding.metaVotes.isVisible = movie.voteCount.isNotBlank()
        binding.scoreCluster.isVisible = true
        binding.fab.isVisible = true
        binding.collapsingToolbar.title = movie.originalTitle
        binding.titleWrite.text = movie.originalTitle
        binding.releaseWrite.text = displayDate(movie.releaseDate)
        binding.metaLanguage.text = movie.originalLanguage.uppercase(Locale.US)
        binding.metaVotes.text = formatVotes(movie.voteCount)
        binding.ratingWrite.text = getString(R.string.rating_out_of_ten, movie.voteAverage)
        binding.ratingRing.setProgressCompat((movie.voteAverage * 10f).toInt().coerceIn(0, 100), true)
        overviewText = movie.overview
        applyOverview()
        binding.posterDetails.load(movie.posterPath.ifBlank { null }) {
            crossfade(true)
            placeholder(R.drawable.ic_poster_placeholder)
        }
        binding.backdrop.load(movie.backdropPath.ifBlank { movie.posterPath.ifBlank { null } }) {
            crossfade(true)
            placeholder(R.drawable.ic_poster_placeholder)
        }
        binding.fab.setImageResource(
            if (state.isFavourite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        trailerAdapter.submitList(state.trailers)
        reviewAdapter.submitList(state.reviews)
        binding.playTrailer.isVisible = state.trailers.isNotEmpty()
        binding.trailersCard.isVisible = state.trailers.isNotEmpty()
        binding.reviewsCard.isVisible = state.reviews.isNotEmpty()
        binding.toolbar.menu.findItem(R.id.action_share)?.isVisible = state.trailers.isNotEmpty()
    }

    private fun applyOverview() {
        val text = overviewText
        binding.overviewNew.text = text.ifBlank { getString(R.string.empty_details) }
        val canCollapse = text.length > OVERVIEW_COLLAPSE_AT
        binding.overviewToggle.isVisible = canCollapse
        binding.overviewNew.maxLines = if (overviewExpanded || !canCollapse) Int.MAX_VALUE else 4
        binding.overviewToggle.setText(if (overviewExpanded) R.string.show_less else R.string.show_more)
    }

    private fun displayDate(raw: String): String {
        val year = raw.take(4)
        return if (year.length == 4 && year.all { it.isDigit() }) year else raw
    }

    private fun formatVotes(raw: String): String {
        val count = raw.toIntOrNull() ?: return raw
        val compact = when {
            count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000f)
            count >= 1_000 -> String.format(Locale.US, "%.1fK", count / 1_000f)
            else -> count.toString()
        }
        return getString(R.string.votes_format, compact)
    }

    private fun shareFirstTrailer() {
        val trailer = viewModel.uiState.value.trailers.firstOrNull() ?: return
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
                    putExtra(Intent.EXTRA_TEXT, trailer.youtubeUrl)
                },
                getString(R.string.share_via)
            )
        )
    }

    private fun openTrailer(trailer: Trailer) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(trailer.youtubeUrl)))
    }

    private fun movieFromIntent(): Movie? {
        val extras = requireActivity().intent.extras ?: return null
        return if (Build.VERSION.SDK_INT >= 33) {
            extras.getParcelable(EXTRA_MOVIE, Movie::class.java)
        } else {
            @Suppress("DEPRECATION")
            extras.getParcelable(EXTRA_MOVIE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_MOVIE = "movie"
        private const val OVERVIEW_COLLAPSE_AT = 220
    }
}
