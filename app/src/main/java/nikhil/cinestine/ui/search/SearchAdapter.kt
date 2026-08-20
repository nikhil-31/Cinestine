package nikhil.cinestine.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.databinding.CustomGridPopularBinding
import nikhil.cinestine.model.Movie
import nikhil.cinestine.model.SearchHit
import nikhil.cinestine.ui.SaveConfetti

class SearchAdapter(
    private val onTitle: (Movie) -> Unit,
    private val onPerson: (String, String) -> Unit,
    private val onCollection: (String, String) -> Unit,
    private val onSave: (Movie) -> Unit
) : ListAdapter<SearchHit, SearchAdapter.Holder>(Diff) {

    var favouriteKeys: Set<String> = emptySet()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = CustomGridPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, onTitle, onPerson, onCollection, onSave)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), favouriteKeys)
    }

    class Holder(
        private val binding: CustomGridPopularBinding,
        private val onTitle: (Movie) -> Unit,
        private val onPerson: (String, String) -> Unit,
        private val onCollection: (String, String) -> Unit,
        private val onSave: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hit: SearchHit, favouriteKeys: Set<String>) {
            binding.popularText.text = hit.title
            binding.popularImage.load(hit.imagePath.ifBlank { null }) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
                error(R.drawable.ic_poster_placeholder)
            }
            binding.mediaTypeBadge.isVisible = !hit.badge.isNullOrBlank()
            binding.mediaTypeBadge.text = hit.badge
            val movie = hit.movie
            binding.popularRatingBadge.isVisible = movie != null && hit.rating != null
            if (hit.rating != null) {
                binding.popularRating.text = binding.root.context.getString(R.string.rating_format, hit.rating)
            }
            binding.saveButton.isVisible = movie != null
            binding.root.setOnClickListener(null)
            binding.saveButton.setOnClickListener(null)
            if (movie != null) {
                val saved = movie.favouriteKey in favouriteKeys
                binding.saveButton.setImageResource(
                    if (saved) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                )
                binding.saveButton.setOnClickListener {
                    if (!saved) SaveConfetti.burstFrom(binding.saveButton)
                    onSave(movie)
                }
                binding.root.setOnClickListener { onTitle(movie) }
            } else if (hit.personId != null) {
                binding.root.setOnClickListener { onPerson(hit.personId, hit.title) }
            } else if (hit.collectionId != null) {
                binding.root.setOnClickListener { onCollection(hit.collectionId, hit.title) }
            }
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<SearchHit>() {
            override fun areItemsTheSame(oldItem: SearchHit, newItem: SearchHit) = oldItem.key == newItem.key
            override fun areContentsTheSame(oldItem: SearchHit, newItem: SearchHit) = oldItem == newItem
        }
    }
}
