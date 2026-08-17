package nikhil.cinestine.ui.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.databinding.CustomGridPopularBinding
import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie

data class MovieListItem(
    val movie: Movie,
    val isFavourite: Boolean = false,
    val showTypeBadge: Boolean = false
)

class MovieAdapter(
    private val onMovieSelected: (Movie) -> Unit,
    private val onSaveClicked: ((Movie) -> Unit)? = null
) : ListAdapter<MovieListItem, MovieAdapter.MovieViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = CustomGridPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding, onMovieSelected, onSaveClicked)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieViewHolder(
        private val binding: CustomGridPopularBinding,
        private val onMovieSelected: (Movie) -> Unit,
        private val onSaveClicked: ((Movie) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovieListItem) {
            val movie = item.movie
            binding.popularText.text = movie.originalTitle
            binding.popularRating.text = binding.root.context.getString(R.string.rating_format, movie.voteAverage)
            binding.popularImage.load(movie.posterPath.ifBlank { null }) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
                error(R.drawable.ic_poster_placeholder)
            }
            binding.root.setOnClickListener { onMovieSelected(movie) }
            val canSave = onSaveClicked != null
            binding.saveButton.isVisible = canSave
            if (canSave) {
                binding.saveButton.setImageResource(
                    if (item.isFavourite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                )
                binding.saveButton.contentDescription = binding.root.context.getString(
                    if (item.isFavourite) R.string.unsave_movie else R.string.save_movie
                )
                binding.saveButton.setOnClickListener { onSaveClicked.invoke(movie) }
            }
            binding.mediaTypeBadge.isVisible = item.showTypeBadge
            if (item.showTypeBadge) {
                binding.mediaTypeBadge.setText(
                    if (movie.mediaType == MediaType.TV) R.string.type_tv else R.string.type_movie
                )
            }
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<MovieListItem>() {
            override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem) =
                oldItem.movie.favouriteKey == newItem.movie.favouriteKey

            override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem) = oldItem == newItem
        }
    }
}
