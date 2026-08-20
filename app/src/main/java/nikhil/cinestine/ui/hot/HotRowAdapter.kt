package nikhil.cinestine.ui.hot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nikhil.cinestine.R
import nikhil.cinestine.databinding.RecyclerHotRowBinding
import nikhil.cinestine.model.Movie
import nikhil.cinestine.ui.movie.MovieAdapter
import nikhil.cinestine.ui.movie.MovieListItem

class HotRowAdapter(
    private val onMovieSelected: (Movie) -> Unit,
    private val onSaveClicked: (Movie) -> Unit,
    private val onSeeAll: (HotRow) -> Unit,
    private val onNearEnd: (HotRow) -> Unit
) : ListAdapter<HotRow, HotRowAdapter.RowViewHolder>(Diff) {

    var favouriteKeys: Set<String> = emptySet()
        set(value) {
            if (field == value) return
            field = value
            if (itemCount > 0) notifyItemRangeChanged(0, itemCount, PAYLOAD_FAVOURITES)
        }

    private var recentFirstKey: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = RecyclerHotRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding, onMovieSelected, onSaveClicked, onSeeAll, onNearEnd)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val row = getItem(position)
        holder.bind(row, favouriteKeys, shouldScrollRecent(row))
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_FAVOURITES)) {
            holder.bindFavourites(favouriteKeys)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class RowViewHolder(
        private val binding: RecyclerHotRowBinding,
        onMovieSelected: (Movie) -> Unit,
        onSaveClicked: (Movie) -> Unit,
        private val onSeeAll: (HotRow) -> Unit,
        private val onNearEnd: (HotRow) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val movieAdapter = MovieAdapter(onMovieSelected, onSaveClicked, R.dimen.hot_poster_width)
        private val layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        private var row: HotRow? = null

        init {
            layoutManager.initialPrefetchItemCount = 6
            binding.recyclerPosters.layoutManager = layoutManager
            binding.recyclerPosters.adapter = movieAdapter
            binding.recyclerPosters.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dx <= 0) return
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val current = row ?: return
                    if (lastVisible >= movieAdapter.itemCount - 4) {
                        onNearEnd(current)
                    }
                }
            })
        }

        fun bind(row: HotRow, favouriteKeys: Set<String>, scrollToLatest: Boolean) {
            this.row = row
            binding.rowTitle.setText(row.titleRes)
            binding.rowSeeAll.isVisible = row.showSeeAll
            if (row.showSeeAll) {
                binding.rowHeader.setOnClickListener { onSeeAll(row) }
                binding.rowSeeAll.setOnClickListener { onSeeAll(row) }
            } else {
                binding.rowHeader.setOnClickListener(null)
                binding.rowSeeAll.setOnClickListener(null)
            }
            movieAdapter.submitList(
                row.movies.map { movie ->
                    MovieListItem(movie, movie.favouriteKey in favouriteKeys)
                }
            ) {
                if (scrollToLatest) {
                    binding.recyclerPosters.smoothScrollToPosition(0)
                }
            }
        }

        fun bindFavourites(favouriteKeys: Set<String>) {
            movieAdapter.applyFavouriteKeys(favouriteKeys)
        }
    }

    private fun shouldScrollRecent(row: HotRow): Boolean {
        if (row.showSeeAll) return false
        val firstKey = row.movies.firstOrNull()?.favouriteKey
        val scroll = firstKey != null && recentFirstKey != null && firstKey != recentFirstKey
        recentFirstKey = firstKey
        return scroll
    }

    private companion object {
        const val PAYLOAD_FAVOURITES = "favourites"
        val Diff = object : DiffUtil.ItemCallback<HotRow>() {
            override fun areItemsTheSame(oldItem: HotRow, newItem: HotRow) = oldItem.key == newItem.key
            override fun areContentsTheSame(oldItem: HotRow, newItem: HotRow) = oldItem == newItem
        }
    }
}
