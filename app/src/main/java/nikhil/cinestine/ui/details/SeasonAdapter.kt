package nikhil.cinestine.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.databinding.RecyclerSeasonRowBinding
import nikhil.cinestine.model.Season

data class SeasonListItem(
    val season: Season,
    val selected: Boolean
)

class SeasonAdapter(
    private val onSeasonSelected: (Season) -> Unit
) : ListAdapter<SeasonListItem, SeasonAdapter.SeasonViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val binding = RecyclerSeasonRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeasonViewHolder(binding, onSeasonSelected)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SeasonViewHolder(
        private val binding: RecyclerSeasonRowBinding,
        private val onSeasonSelected: (Season) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SeasonListItem) {
            val season = item.season
            binding.seasonName.text = season.name
            binding.seasonEpisodes.text = binding.root.resources.getQuantityString(
                R.plurals.episode_count,
                season.episodeCount,
                season.episodeCount
            )
            binding.seasonPoster.load(season.posterPath.ifBlank { null }) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
                error(R.drawable.ic_poster_placeholder)
            }
            binding.root.strokeWidth = if (item.selected) 3 else 0
            binding.root.strokeColor = binding.root.context.getColor(R.color.colorPrimary)
            binding.root.setOnClickListener { onSeasonSelected(season) }
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<SeasonListItem>() {
            override fun areItemsTheSame(oldItem: SeasonListItem, newItem: SeasonListItem) =
                oldItem.season.id == newItem.season.id

            override fun areContentsTheSame(oldItem: SeasonListItem, newItem: SeasonListItem) = oldItem == newItem
        }
    }
}
