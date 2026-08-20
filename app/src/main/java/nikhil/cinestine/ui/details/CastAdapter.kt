package nikhil.cinestine.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import nikhil.cinestine.R
import nikhil.cinestine.databinding.RecyclerCastRowBinding
import nikhil.cinestine.model.CastMember

class CastAdapter(
    private val onCastSelected: (CastMember) -> Unit
) : ListAdapter<CastMember, CastAdapter.CastViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = RecyclerCastRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CastViewHolder(binding, onCastSelected)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CastViewHolder(
        private val binding: RecyclerCastRowBinding,
        private val onCastSelected: (CastMember) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: CastMember) {
            binding.castName.text = member.name
            binding.castCharacter.text = member.character
            binding.castPhoto.load(member.profilePath.ifBlank { null }) {
                crossfade(true)
                transformations(CircleCropTransformation())
                placeholder(R.drawable.avatar_bg)
                error(R.drawable.avatar_bg)
            }
            binding.root.setOnClickListener { onCastSelected(member) }
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<CastMember>() {
            override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember) = oldItem == newItem
        }
    }
}
