package nikhil.cinestine.ui.details

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.databinding.RecyclerTrailerSingleRowBinding
import nikhil.cinestine.model.Trailer

class TrailerAdapter : ListAdapter<Trailer, TrailerAdapter.TrailerViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        val binding = RecyclerTrailerSingleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrailerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrailerViewHolder(
        private val binding: RecyclerTrailerSingleRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trailer: Trailer) {
            binding.trailerSingleRowText.text = trailer.name
            binding.trailerSingleRowImage.load(trailer.thumbnailUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
            }
            binding.root.setOnClickListener {
                it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(trailer.youtubeUrl)))
            }
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<Trailer>() {
            override fun areItemsTheSame(oldItem: Trailer, newItem: Trailer) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Trailer, newItem: Trailer) = oldItem == newItem
        }
    }
}
