package nikhil.cinestine.ui.discover

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import nikhil.cinestine.R
import nikhil.cinestine.model.Movie
import nikhil.cinestine.ui.details.DetailsActivity
import nikhil.cinestine.ui.details.DetailsFragment
import nikhil.cinestine.ui.main.MovieSelectionListener

class DiscoverResultsActivity : AppCompatActivity(), MovieSelectionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_results)
    }

    override fun onMovieSelected(movie: Movie) {
        startActivity(Intent(this, DetailsActivity::class.java).putExtra(DetailsFragment.EXTRA_MOVIE, movie))
    }
}
