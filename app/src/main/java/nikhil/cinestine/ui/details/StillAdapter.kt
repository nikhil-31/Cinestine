package nikhil.cinestine.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.databinding.RecyclerStillRowBinding
import nikhil.cinestine.model.MediaImage

class StillAdapter(
    private val onImageSelected: (Int) -> Unit
) : ListAdapter<MediaImage, StillAdapter.StillViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StillViewHolder {
        val binding = RecyclerStillRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StillViewHolder(binding, onImageSelected)
    }

    override fun onBindViewHolder(holder: StillViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class StillViewHolder(
        private val binding: RecyclerStillRowBinding,
        private val onImageSelected: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: MediaImage, position: Int) {
            binding.stillImage.load(image.url.ifBlank { null }) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
            }
            binding.root.setOnClickListener { onImageSelected(position) }
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<MediaImage>() {
            override fun areItemsTheSame(oldItem: MediaImage, newItem: MediaImage) = oldItem.url == newItem.url
            override fun areContentsTheSame(oldItem: MediaImage, newItem: MediaImage) = oldItem == newItem
        }
    }
}
