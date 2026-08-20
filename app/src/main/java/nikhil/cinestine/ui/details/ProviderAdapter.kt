package nikhil.cinestine.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import nikhil.cinestine.R
import nikhil.cinestine.databinding.RecyclerProviderRowBinding
import nikhil.cinestine.model.WatchProvider

class ProviderAdapter : ListAdapter<WatchProvider, ProviderAdapter.ProviderViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val binding = RecyclerProviderRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProviderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProviderViewHolder(
        private val binding: RecyclerProviderRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(provider: WatchProvider) {
            binding.providerLogo.contentDescription = provider.name
            binding.providerLogo.load(provider.logoPath.ifBlank { null }) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
            }
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<WatchProvider>() {
            override fun areItemsTheSame(oldItem: WatchProvider, newItem: WatchProvider) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: WatchProvider, newItem: WatchProvider) = oldItem == newItem
        }
    }
}
