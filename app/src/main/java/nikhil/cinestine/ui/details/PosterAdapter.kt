package nikhil.cinestine.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.databinding.RecyclerPosterRowBinding
import nikhil.cinestine.model.Movie

class PosterAdapter(
    private val onMovieSelected: (Movie) -> Unit
) : ListAdapter<Movie, PosterAdapter.PosterViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val binding = RecyclerPosterRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PosterViewHolder(binding, onMovieSelected)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PosterViewHolder(
        private val binding: RecyclerPosterRowBinding,
        private val onMovieSelected: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.posterTitle.text = movie.originalTitle
            binding.posterImage.load(movie.posterPath.ifBlank { null }) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
                error(R.drawable.ic_poster_placeholder)
            }
            binding.root.setOnClickListener { onMovieSelected(movie) }
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem.favouriteKey == newItem.favouriteKey

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
        }
    }
}
