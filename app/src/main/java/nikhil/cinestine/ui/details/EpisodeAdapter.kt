package nikhil.cinestine.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.databinding.RecyclerEpisodeRowBinding
import nikhil.cinestine.model.Episode

class EpisodeAdapter(
    private val onEpisodeSelected: (Episode) -> Unit
) : ListAdapter<Episode, EpisodeAdapter.EpisodeViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = RecyclerEpisodeRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EpisodeViewHolder(binding, onEpisodeSelected)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EpisodeViewHolder(
        private val binding: RecyclerEpisodeRowBinding,
        private val onEpisodeSelected: (Episode) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(episode: Episode) {
            binding.episodeTitle.text = episode.name.ifBlank {
                binding.root.context.getString(R.string.episode_fallback, episode.episodeNumber)
            }
            binding.episodeCode.text = binding.root.context.getString(
                R.string.episode_code,
                episode.seasonNumber,
                episode.episodeNumber
            )
            val meta = listOfNotNull(
                episode.airDate.takeIf { it.isNotBlank() },
                episode.runtimeMinutes?.let { binding.root.context.getString(R.string.runtime_minutes, it) }
            ).joinToString("  ·  ")
            binding.episodeMeta.text = meta
            binding.episodeMeta.isVisible = meta.isNotBlank()
            binding.episodeOverview.text = episode.overview
            binding.episodeOverview.isVisible = episode.overview.isNotBlank()
            binding.episodeStill.load(episode.stillPath.ifBlank { null }) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
                error(R.drawable.ic_poster_placeholder)
            }
            binding.root.setOnClickListener { onEpisodeSelected(episode) }
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<Episode>() {
            override fun areItemsTheSame(oldItem: Episode, newItem: Episode) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Episode, newItem: Episode) = oldItem == newItem
        }
    }
}
