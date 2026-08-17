package nikhil.cinestine.ui.main

import nikhil.cinestine.model.MediaType
import nikhil.cinestine.model.Movie

fun interface MovieSelectionListener {
    fun onMovieSelected(movie: Movie)
}

interface BrowseScrollHost {
    fun onBrowseListScrolled(dy: Int)
    fun onBrowseScrollSettled()
}

fun interface MediaTypeHost {
    fun onBrowseMediaTypeChanged(mediaType: MediaType)
}
