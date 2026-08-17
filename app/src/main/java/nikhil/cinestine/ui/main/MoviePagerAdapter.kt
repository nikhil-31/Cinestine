package nikhil.cinestine.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import nikhil.cinestine.model.MovieCategory
import nikhil.cinestine.ui.favourites.FavouritesFragment
import nikhil.cinestine.ui.hot.HotFragment
import nikhil.cinestine.ui.movie.MovieListFragment

class MoviePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> MovieListFragment.newInstance(MovieCategory.POPULAR)
        1 -> HotFragment()
        2 -> MovieListFragment.newInstance(MovieCategory.TOP_RATED)
        else -> FavouritesFragment()
    }
}
