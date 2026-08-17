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
import com.google.android.material.chip.Chip
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.FragmentDetailsBinding
import nikhil.cinestine.model.CastMember
import nikhil.cinestine.model.DiscoverFilter
import nikhil.cinestine.model.Episode
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.TaggedLink
import nikhil.cinestine.model.TitleDetails
import nikhil.cinestine.model.Trailer
import nikhil.cinestine.model.VideoGroup
import nikhil.cinestine.ui.collection.CollectionActivity
import nikhil.cinestine.ui.collection.CollectionFragment
import nikhil.cinestine.ui.discover.DiscoverResultsActivity
import nikhil.cinestine.ui.discover.DiscoverResultsFragment
import nikhil.cinestine.ui.episode.EpisodeActivity
import nikhil.cinestine.ui.episode.EpisodeFragment
import nikhil.cinestine.ui.main.MovieSelectionListener
import nikhil.cinestine.ui.person.PersonActivity
import nikhil.cinestine.ui.person.PersonFragment
import nikhil.cinestine.ui.RegionPreferences
import nikhil.cinestine.ui.SaveConfetti
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
    private val seasonAdapter = SeasonAdapter { season -> viewModel.selectSeason(season.seasonNumber) }
    private val episodeAdapter = EpisodeAdapter { openEpisode(it) }
    private val castAdapter = CastAdapter(::onCastSelected)
    private val similarAdapter = PosterAdapter(::onSimilarSelected)
    private val providerAdapter = ProviderAdapter()
    private val stillAdapter = StillAdapter(::openGallery)
    private var overviewExpanded = false
    private var overviewText = ""
    private var renderedMovieId: String? = null
    private var renderedGenres: List<String> = emptyList()
    private var renderedTopics: List<TaggedLink> = emptyList()
    private var videoGroup: VideoGroup? = null

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
            when (item.itemId) {
                R.id.action_share -> {
                    shareTitle()
                    true
                }
                R.id.action_watch_region -> {
                    pickWatchRegion()
                    true
                }
                else -> false
            }
        }
        binding.watchRegion.setOnClickListener { pickWatchRegion() }

        binding.recyclerTrailer.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerTrailer.adapter = trailerAdapter
        binding.recyclerReview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerReview.adapter = reviewAdapter
        binding.recyclerReview.isNestedScrollingEnabled = false
        binding.recyclerSeasons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerSeasons.adapter = seasonAdapter
        binding.recyclerEpisodes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEpisodes.adapter = episodeAdapter
        binding.recyclerEpisodes.isNestedScrollingEnabled = false
        binding.recyclerCast.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCast.adapter = castAdapter
        binding.recyclerSimilar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerSimilar.adapter = similarAdapter
        binding.recyclerProviders.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerProviders.adapter = providerAdapter
        binding.recyclerPhotos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerPhotos.adapter = stillAdapter
        binding.videoToggle.setOnCheckedStateChangeListener { _, checkedIds ->
            videoGroup = when (checkedIds.firstOrNull()) {
                R.id.chip_video_trailers -> VideoGroup.TRAILER
                R.id.chip_video_teasers -> VideoGroup.TEASER
                R.id.chip_video_clips -> VideoGroup.CLIP
                else -> null
            }
            applyVideoFilter(viewModel.uiState.value.trailers)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.detailsScroll) { scroll, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            scroll.updatePadding(bottom = bars.bottom + resources.getDimensionPixelSize(R.dimen.fab_margin))
            insets
        }

        binding.fab.setOnClickListener {
            if (!viewModel.uiState.value.isFavourite) SaveConfetti.burstFrom(binding.fab)
            viewModel.toggleFavourite()
        }
        binding.playTrailer.setOnClickListener {
            viewModel.uiState.value.trailers.firstOrNull()?.let(::openTrailer)
        }
        binding.overviewToggle.setOnClickListener {
            overviewExpanded = !overviewExpanded
            applyOverview()
        }
        binding.backdrop.setOnClickListener { openGallery(0) }
        binding.providersCard.setOnClickListener {
            val link = viewModel.uiState.value.watch?.link?.takeIf { it.isNotBlank() } ?: return@setOnClickListener
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        }
        binding.collectionCard.setOnClickListener {
            val collection = viewModel.uiState.value.details?.collection ?: return@setOnClickListener
            startActivity(
                Intent(requireContext(), CollectionActivity::class.java)
                    .putExtra(CollectionFragment.EXTRA_COLLECTION_ID, collection.id)
                    .putExtra(CollectionFragment.EXTRA_COLLECTION_NAME, collection.name)
            )
        }
        binding.nextEpisodeCard.setOnClickListener {
            val episode = viewModel.uiState.value.details?.nextEpisode
                ?: viewModel.uiState.value.details?.lastEpisode
                ?: return@setOnClickListener
            openEpisode(episode)
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
            binding.metaRuntime.isVisible = false
            binding.metaSeasons.isVisible = false
            binding.metaEpisodes.isVisible = false
            binding.scoreCluster.isVisible = false
            binding.playTrailer.isVisible = false
            binding.taglineWrite.isVisible = false
            binding.genreChips.isVisible = false
            binding.networksWrite.isVisible = false
            binding.nextEpisodeCard.isVisible = false
            binding.seasonsCard.isVisible = false
            binding.episodesCard.isVisible = false
            binding.trailersCard.isVisible = false
            binding.reviewsCard.isVisible = false
            binding.castCard.isVisible = false
            binding.providersCard.isVisible = false
            binding.photosCard.isVisible = false
            binding.similarCard.isVisible = false
            binding.collectionCard.isVisible = false
            binding.topicsCard.isVisible = false
            binding.metaCertification.isVisible = false
            binding.fab.isVisible = false
            binding.toolbar.menu.findItem(R.id.action_share)?.isVisible = false
            binding.toolbar.menu.findItem(R.id.action_watch_region)?.isVisible = false
            return
        }

        val movieChanged = movie.favouriteKey != renderedMovieId
        if (movieChanged) {
            renderedMovieId = movie.favouriteKey
            overviewExpanded = false
            renderedGenres = emptyList()
            renderedTopics = emptyList()
            videoGroup = null
            binding.genreChips.removeAllViews()
            binding.topicChips.removeAllViews()
            binding.videoToggle.check(R.id.chip_video_all)
        }

        val details = state.details
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
        bindDetails(movie, details)
        overviewText = details?.overview?.takeIf { it.isNotBlank() } ?: movie.overview
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
        applyVideoFilter(state.trailers)
        reviewAdapter.submitList(state.reviews)
        val playable = state.trailers.firstOrNull { it.group == VideoGroup.TRAILER } ?: state.trailers.firstOrNull()
        binding.playTrailer.isVisible = playable != null
        binding.trailersCard.isVisible = state.trailers.isNotEmpty()
        binding.reviewsCard.isVisible = state.reviews.isNotEmpty()
        binding.castCard.isVisible = state.cast.isNotEmpty()
        castAdapter.submitList(state.cast)
        binding.similarCard.isVisible = state.recommendations.isNotEmpty()
        similarAdapter.submitList(state.recommendations)
        binding.providersCard.isVisible = true
        val providers = state.watch?.providers.orEmpty()
        providerAdapter.submitList(providers)
        binding.recyclerProviders.isVisible = state.watch != null && providers.isNotEmpty()
        binding.watchEmpty.isVisible = state.watch != null && providers.isEmpty()
        binding.watchRegion.text = requireContext().cinestineApp.repository.currentRegion()
        binding.photosCard.isVisible = state.images.isNotEmpty()
        stillAdapter.submitList(state.images)
        binding.toolbar.menu.findItem(R.id.action_share)?.isVisible = true
        binding.toolbar.menu.findItem(R.id.action_watch_region)?.isVisible = true
        bindTv(state)
    }

    private fun bindDetails(movie: Movie, details: TitleDetails?) {
        val tagline = details?.tagline.orEmpty()
        binding.taglineWrite.isVisible = tagline.isNotBlank()
        binding.taglineWrite.text = tagline

        val runtime = details?.runtimeMinutes
        binding.metaRuntime.isVisible = runtime != null
        if (runtime != null) {
            binding.metaRuntime.text = if (movie.mediaType == MediaType.TV) {
                getString(R.string.runtime_episode, runtime)
            } else {
                formatRuntime(runtime)
            }
        }

        val seasonCount = details?.seasonCount ?: 0
        binding.metaSeasons.isVisible = movie.mediaType == MediaType.TV && seasonCount > 0
        if (seasonCount > 0) {
            binding.metaSeasons.text = resources.getQuantityString(R.plurals.season_count, seasonCount, seasonCount)
        }
        val episodeCount = details?.episodeCount ?: 0
        binding.metaEpisodes.isVisible = movie.mediaType == MediaType.TV && episodeCount > 0
        if (episodeCount > 0) {
            binding.metaEpisodes.text = resources.getQuantityString(R.plurals.episode_count, episodeCount, episodeCount)
        }

        val genres = details?.genres.orEmpty()
        binding.genreChips.isVisible = genres.isNotEmpty()
        if (genres != renderedGenres) {
            renderedGenres = genres
            binding.genreChips.removeAllViews()
            genres.forEach { name ->
                binding.genreChips.addView(
                    Chip(requireContext()).apply {
                        text = name
                        isClickable = false
                        isCheckable = false
                    }
                )
            }
        }

        val certification = details?.certification.orEmpty()
        binding.metaCertification.isVisible = certification.isNotBlank()
        binding.metaCertification.text = certification

        val networkLinks = details?.networkLinks.orEmpty()
        val networks = details?.networks.orEmpty()
        binding.networksWrite.isVisible = networks.isNotEmpty() && networkLinks.isEmpty()
        if (binding.networksWrite.isVisible) {
            binding.networksWrite.text = getString(R.string.networks_format, networks.joinToString(" · "))
        }

        val collection = details?.collection
        binding.collectionCard.isVisible = collection != null
        if (collection != null) {
            binding.collectionName.text = getString(R.string.part_of_collection, collection.name)
            binding.collectionPoster.load(collection.posterPath.ifBlank { collection.backdropPath.ifBlank { null } }) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
            }
        }

        val topics = details?.keywords.orEmpty() + details?.companies.orEmpty() + networkLinks
        binding.topicsCard.isVisible = topics.isNotEmpty()
        if (topics != renderedTopics) {
            renderedTopics = topics
            binding.topicChips.removeAllViews()
            topics.forEach { link ->
                binding.topicChips.addView(
                    Chip(requireContext()).apply {
                        text = link.name
                        isClickable = true
                        isCheckable = false
                        setOnClickListener { openDiscover(link) }
                    }
                )
            }
        }

        val spotlight = details?.nextEpisode ?: details?.lastEpisode
        binding.nextEpisodeCard.isVisible = spotlight != null
        if (spotlight != null) {
            bindSpotlightEpisode(spotlight, isNext = details?.nextEpisode != null)
        }
    }

    private fun bindSpotlightEpisode(episode: Episode, isNext: Boolean) {
        binding.nextEpisodeLabel.setText(if (isNext) R.string.next_episode else R.string.latest_episode)
        val title = episode.name.ifBlank { getString(R.string.episode_fallback, episode.episodeNumber) }
        binding.nextEpisodeTitle.text = getString(
            R.string.episode_code,
            episode.seasonNumber,
            episode.episodeNumber
        ) + "  ·  " + title
        binding.nextEpisodeMeta.isVisible = episode.airDate.isNotBlank()
        binding.nextEpisodeMeta.text = episode.airDate
    }

    private fun bindTv(state: DetailsUiState) {
        val isTv = state.movie?.mediaType == MediaType.TV
        binding.seasonsCard.isVisible = isTv && state.seasons.isNotEmpty()
        binding.episodesCard.isVisible = isTv && (state.episodes.isNotEmpty() || state.episodesLoading)
        if (!isTv) return
        seasonAdapter.submitList(
            state.seasons.map { SeasonListItem(it, it.seasonNumber == state.selectedSeason) }
        )
        binding.episodesProgress.isVisible = state.episodesLoading && state.episodes.isEmpty()
        episodeAdapter.submitList(state.episodes)
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

    private fun formatRuntime(minutes: Int): String {
        val hours = minutes / 60
        val remaining = minutes % 60
        return when {
            hours > 0 && remaining > 0 -> getString(R.string.runtime_hours_minutes, hours, remaining)
            hours > 0 -> getString(R.string.runtime_hours, hours)
            else -> getString(R.string.runtime_minutes, minutes)
        }
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

    private fun applyVideoFilter(trailers: List<Trailer>) {
        val filtered = videoGroup?.let { group -> trailers.filter { it.group == group } } ?: trailers
        trailerAdapter.submitList(filtered)
        binding.videoToggle.isVisible = trailers.map { it.group }.distinct().size > 1
    }

    private fun onCastSelected(member: CastMember) {
        startActivity(
            Intent(requireContext(), PersonActivity::class.java)
                .putExtra(PersonFragment.EXTRA_PERSON_ID, member.id)
                .putExtra(PersonFragment.EXTRA_PERSON_NAME, member.name)
        )
    }

    private fun openEpisode(episode: Episode) {
        val movie = viewModel.uiState.value.movie ?: return
        if (movie.mediaType != MediaType.TV) return
        startActivity(
            Intent(requireContext(), EpisodeActivity::class.java)
                .putExtra(EpisodeFragment.EXTRA_TV_ID, movie.id)
                .putExtra(EpisodeFragment.EXTRA_SEASON, episode.seasonNumber)
                .putExtra(EpisodeFragment.EXTRA_EPISODE, episode.episodeNumber)
                .putExtra(EpisodeFragment.EXTRA_SHOW_TITLE, movie.originalTitle)
        )
    }

    private fun openDiscover(link: TaggedLink) {
        val movie = viewModel.uiState.value.movie ?: return
        val mediaType = if (link.kind == TaggedLink.Kind.NETWORK) MediaType.TV else movie.mediaType
        val filter = when (link.kind) {
            TaggedLink.Kind.KEYWORD -> DiscoverFilter(keywordIds = setOf(link.id))
            TaggedLink.Kind.COMPANY -> DiscoverFilter(companyIds = setOf(link.id))
            TaggedLink.Kind.NETWORK -> DiscoverFilter(networkIds = setOf(link.id))
        }
        startActivity(
            Intent(requireContext(), DiscoverResultsActivity::class.java)
                .putExtra(DiscoverResultsFragment.EXTRA_TITLE, link.name)
                .putExtra(DiscoverResultsFragment.EXTRA_MEDIA_TYPE, mediaType.name)
                .putExtra(DiscoverResultsFragment.EXTRA_FILTER, filter)
        )
    }

    private fun onSimilarSelected(movie: Movie) {
        val host = activity as? MovieSelectionListener
        if (host != null && activity !is DetailsActivity) {
            host.onMovieSelected(movie)
        } else {
            startActivity(Intent(requireContext(), DetailsActivity::class.java).putExtra(EXTRA_MOVIE, movie))
        }
    }

    private fun openGallery(startIndex: Int) {
        val urls = viewModel.uiState.value.images.map { it.url }.filter { it.isNotBlank() }
        if (urls.isEmpty()) return
        GalleryDialogFragment.newInstance(urls, startIndex)
            .show(parentFragmentManager, "gallery")
    }

    private fun shareTitle() {
        val movie = viewModel.uiState.value.movie ?: return
        val path = if (movie.mediaType == MediaType.TV) "tv" else "movie"
        val url = "https://www.themoviedb.org/$path/${movie.id}"
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, movie.title)
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_title_text, movie.title, url))
                },
                getString(R.string.share_via)
            )
        )
    }

    private fun pickWatchRegion() {
        RegionPreferences.showPicker(requireContext()) {
            binding.watchRegion.text = requireContext().cinestineApp.repository.currentRegion()
            viewModel.reloadRegionSensitive()
        }
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
