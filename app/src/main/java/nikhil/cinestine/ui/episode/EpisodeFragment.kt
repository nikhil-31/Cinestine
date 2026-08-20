package nikhil.cinestine.ui.episode

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
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.FragmentEpisodeBinding
import nikhil.cinestine.model.CastMember
import nikhil.cinestine.ui.details.CastAdapter
import nikhil.cinestine.ui.details.GalleryDialogFragment
import nikhil.cinestine.ui.details.StillAdapter
import nikhil.cinestine.ui.details.TrailerAdapter
import nikhil.cinestine.ui.person.PersonActivity
import nikhil.cinestine.ui.person.PersonFragment
import kotlinx.coroutines.launch

class EpisodeFragment : Fragment() {

    private var _binding: FragmentEpisodeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EpisodeViewModel by viewModels {
        EpisodeViewModel.Factory(requireContext().cinestineApp.repository)
    }

    private val guestAdapter = CastAdapter(::onGuestSelected)
    private val videoAdapter = TrailerAdapter()
    private val stillAdapter = StillAdapter(::openGallery)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEpisodeBinding.inflate(inflater, container, false)
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
        binding.recyclerGuests.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerGuests.adapter = guestAdapter
        binding.recyclerVideos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerVideos.adapter = videoAdapter
        binding.recyclerPhotos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerPhotos.adapter = stillAdapter

        val intent = requireActivity().intent
        val showTitle = intent.getStringExtra(EXTRA_SHOW_TITLE).orEmpty()
        if (showTitle.isNotBlank()) binding.toolbar.title = showTitle
        val tvId = intent.getStringExtra(EXTRA_TV_ID).orEmpty()
        val season = intent.getIntExtra(EXTRA_SEASON, 0)
        val episode = intent.getIntExtra(EXTRA_EPISODE, 0)
        if (tvId.isNotBlank() && season > 0 && episode > 0) {
            viewModel.load(tvId, season, episode)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: EpisodeUiState) {
        binding.progress.isVisible = state.isLoading && state.details == null
        binding.errorText.isVisible = state.error != null && state.details == null
        binding.errorText.text = state.error
        val details = state.details
        binding.episodeContent.isVisible = details != null
        if (details == null) return
        val episode = details.episode
        val title = episode.name.ifBlank { getString(R.string.episode_fallback, episode.episodeNumber) }
        binding.toolbar.title = title
        binding.episodeTitle.text = title
        binding.episodeCode.text = getString(R.string.episode_code, episode.seasonNumber, episode.episodeNumber)
        val meta = listOfNotNull(
            episode.airDate.takeIf { it.isNotBlank() },
            episode.runtimeMinutes?.let { getString(R.string.runtime_minutes, it) }
        ).joinToString("  ·  ")
        binding.episodeMeta.isVisible = meta.isNotBlank()
        binding.episodeMeta.text = meta
        binding.episodeStill.isVisible = episode.stillPath.isNotBlank()
        binding.episodeStill.load(episode.stillPath.ifBlank { null }) {
            crossfade(true)
            placeholder(R.drawable.ic_poster_placeholder)
        }
        binding.overviewCard.isVisible = episode.overview.isNotBlank()
        binding.episodeOverview.text = episode.overview
        binding.guestsCard.isVisible = details.guestStars.isNotEmpty()
        guestAdapter.submitList(details.guestStars)
        binding.videosCard.isVisible = details.videos.isNotEmpty()
        videoAdapter.submitList(details.videos)
        binding.photosCard.isVisible = details.images.isNotEmpty()
        stillAdapter.submitList(details.images)
    }

    private fun onGuestSelected(member: CastMember) {
        startActivity(
            Intent(requireContext(), PersonActivity::class.java)
                .putExtra(PersonFragment.EXTRA_PERSON_ID, member.id)
                .putExtra(PersonFragment.EXTRA_PERSON_NAME, member.name)
        )
    }

    private fun openGallery(startIndex: Int) {
        val urls = viewModel.uiState.value.details?.images.orEmpty().map { it.url }.filter { it.isNotBlank() }
        if (urls.isEmpty()) return
        GalleryDialogFragment.newInstance(urls, startIndex)
            .show(parentFragmentManager, "gallery")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_TV_ID = "tv_id"
        const val EXTRA_SEASON = "season"
        const val EXTRA_EPISODE = "episode"
        const val EXTRA_SHOW_TITLE = "show_title"
    }
}
