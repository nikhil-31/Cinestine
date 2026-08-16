package nikhil.cinestine.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nikhil.cinestine.R
import nikhil.cinestine.databinding.RecyclerReviewSingleRowBinding
import nikhil.cinestine.model.Review

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = RecyclerReviewSingleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(
        private val binding: RecyclerReviewSingleRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            val author = review.author.ifBlank { "?" }
            binding.reviewSingleRowAuthor.text = binding.root.context.getString(R.string.author_format, author)
            binding.reviewAuthorInitial.text = author.first().uppercaseChar().toString()
            binding.reviewSingleRowContent.text = review.content
        }
    }

    private companion object {
        val Diff = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Review, newItem: Review) = oldItem == newItem
        }
    }
}
