package nikhil.cinestine.ui.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.databinding.CustomGridPopularBinding
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie
import nikhil.cinestine.ui.SaveConfetti

data class MovieListItem(
    val movie: Movie,
    val isFavourite: Boolean = false,
    val showTypeBadge: Boolean = false
)

class MovieAdapter(
    private val onMovieSelected: (Movie) -> Unit,
    private val onSaveClicked: ((Movie) -> Unit)? = null,
    @param:DimenRes private val itemWidth: Int? = null
) : ListAdapter<MovieListItem, MovieAdapter.MovieViewHolder>(Diff) {

    private var favouriteKeys: Set<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = CustomGridPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        if (itemWidth != null) {
            val width = parent.resources.getDimensionPixelSize(itemWidth)
            val gap = parent.resources.getDimensionPixelSize(R.dimen.grid_item_gap)
            binding.root.layoutParams = RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
                marginEnd = gap * 2
            }
        }
        return MovieViewHolder(binding, onMovieSelected, onSaveClicked)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, isFavourite(item))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_FAVOURITE)) {
            val item = getItem(position)
            holder.bindFavourite(item, isFavourite(item))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun applyFavouriteKeys(keys: Set<String>) {
        favouriteKeys = keys
        if (itemCount > 0) notifyItemRangeChanged(0, itemCount, PAYLOAD_FAVOURITE)
    }

    private fun isFavourite(item: MovieListItem): Boolean {
        return favouriteKeys?.contains(item.movie.favouriteKey) ?: item.isFavourite
    }

    class MovieViewHolder(
        private val binding: CustomGridPopularBinding,
        private val onMovieSelected: (Movie) -> Unit,
        private val onSaveClicked: ((Movie) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovieListItem, isFavourite: Boolean) {
            val movie = item.movie
            binding.popularText.text = movie.originalTitle
            binding.popularRating.text = binding.root.context.getString(R.string.rating_format, movie.voteAverage)
            binding.popularImage.load(movie.posterPath.ifBlank { null }) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
                error(R.drawable.ic_poster_placeholder)
            }
            binding.root.setOnClickListener { onMovieSelected(movie) }
            bindFavourite(item, isFavourite)
            binding.mediaTypeBadge.isVisible = item.showTypeBadge
            if (item.showTypeBadge) {
                binding.mediaTypeBadge.setText(
                    if (movie.mediaType == MediaType.TV) R.string.type_tv else R.string.type_movie
                )
            }
        }

        fun bindFavourite(item: MovieListItem, isFavourite: Boolean) {
            val canSave = onSaveClicked != null
            binding.saveButton.isVisible = canSave
            if (!canSave) return
            val movie = item.movie
            binding.saveButton.setImageResource(
                if (isFavourite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
            binding.saveButton.contentDescription = binding.root.context.getString(
                if (isFavourite) R.string.unsave_movie else R.string.save_movie
            )
            binding.saveButton.setOnClickListener {
                if (!isFavourite) SaveConfetti.burstFrom(binding.saveButton)
                onSaveClicked.invoke(movie)
            }
        }
    }

    private companion object {
        const val PAYLOAD_FAVOURITE = "favourite"
        val Diff = object : DiffUtil.ItemCallback<MovieListItem>() {
            override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem) =
                oldItem.movie.favouriteKey == newItem.movie.favouriteKey

            override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem) = oldItem == newItem
        }
    }
}
