package nikhil.cinestine.ui.main

import nikhil.cinestine.model.Movie

fun interface MovieSelectionListener {
    fun onMovieSelected(movie: Movie)
}

interface BrowseScrollHost {
    fun onBrowseListScrolled(dy: Int)
    fun onBrowseScrollSettled()
}
