package nikhil.cinestine.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import nikhil.cinestine.R
import nikhil.cinestine.cinestineApp
import nikhil.cinestine.databinding.ActivityMainBinding
import nikhil.cinestine.model.Movie
import nikhil.cinestine.ui.ThemePreferences
import nikhil.cinestine.ui.details.DetailsActivity
import nikhil.cinestine.ui.details.DetailsFragment
import nikhil.cinestine.ui.search.SearchFragment
import nikhil.cinestine.ui.search.SearchViewModel

class MainActivity : AppCompatActivity(), MovieSelectionListener, BrowseScrollHost {

    private lateinit var binding: ActivityMainBinding
    private var appBarOffset = 0
    private var searchExpanded = false
    private var searchMenuItem: MenuItem? = null

    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModel.Factory(cinestineApp.repository)
    }

    private val collapseSearchOnBack = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            searchMenuItem?.collapseActionView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        updateToolbarTitle(0)
        onBackPressedDispatcher.addCallback(this, collapseSearchOnBack)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = bars.top)
            view.post { applyAppBarOffset() }
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(bottom = bars.bottom)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.searchContainer) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(bottom = bars.bottom)
            insets
        }

        binding.pager.adapter = MoviePagerAdapter(this)
        binding.pager.post {
            (binding.pager.getChildAt(0) as? RecyclerView)?.isNestedScrollingEnabled = false
        }
        binding.appBar.post { applyAppBarOffset() }
        updateToolbarTitle(binding.pager.currentItem)
        binding.bottomNav.setOnItemSelectedListener { item ->
            binding.pager.currentItem = item.itemId.toPagerIndex()
            true
        }
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNav.selectedItemId = position.toNavId()
                updateToolbarTitle(position)
                setAppBarFullyExpanded(true)
            }
        })

        searchExpanded = savedInstanceState?.getBoolean(STATE_SEARCH_EXPANDED) == true
        if (searchExpanded) {
            showSearchOverlay()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_SEARCH_EXPANDED, searchExpanded)
    }

    override fun onBrowseListScrolled(dy: Int) {
        if (dy == 0) return
        val range = binding.appBar.height
        if (range <= 0) return
        val next = (appBarOffset + dy).coerceIn(0, range)
        if (next == appBarOffset) return
        appBarOffset = next
        applyAppBarOffset()
    }

    override fun onBrowseScrollSettled() {
        val range = binding.appBar.height
        if (range <= 0) return
        appBarOffset = if (appBarOffset > range / 2) range else 0
        applyAppBarOffset()
    }

    private fun setAppBarFullyExpanded(expanded: Boolean) {
        appBarOffset = if (expanded) 0 else binding.appBar.height
        applyAppBarOffset()
    }

    private fun applyAppBarOffset() {
        binding.appBar.translationY = -appBarOffset.toFloat()
        val top = (binding.appBar.height - appBarOffset).coerceAtLeast(0)
        binding.pager.updatePadding(top = top)
        binding.searchContainer.updatePadding(top = top)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchMenuItem = searchItem
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.maxWidth = Int.MAX_VALUE
        searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)?.apply {
            setTextColor(ContextCompat.getColor(this@MainActivity, R.color.on_surface))
            setHintTextColor(ContextCompat.getColor(this@MainActivity, R.color.on_surface_variant))
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewModel.submitQuery(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchViewModel.submitQuery(newText)
                return true
            }
        })
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                showSearchOverlay()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                hideSearchOverlay()
                return true
            }
        })
        if (searchExpanded) {
            searchItem.expandActionView()
            searchView.setQuery(searchViewModel.currentQuery, false)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val themeItem = menu.findItem(R.id.action_theme)
        val night = ThemePreferences.isNight(this)
        themeItem?.setIcon(if (night) R.drawable.ic_light_mode else R.drawable.ic_dark_mode)
        themeItem?.setTitle(if (night) R.string.action_light_mode else R.string.action_dark_mode)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                ThemePreferences.toggleLightDark(this)
                true
            }
            R.id.action_theme_system -> {
                ThemePreferences.setNightMode(this, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSearchOverlay() {
        searchExpanded = true
        collapseSearchOnBack.isEnabled = true
        setAppBarFullyExpanded(true)
        binding.pager.isVisible = false
        binding.bottomNav.isVisible = false
        binding.searchContainer.isVisible = true
        if (supportFragmentManager.findFragmentById(R.id.search_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.search_container, SearchFragment())
                .commitNow()
        }
        applyAppBarOffset()
    }

    private fun hideSearchOverlay() {
        searchExpanded = false
        collapseSearchOnBack.isEnabled = false
        searchViewModel.clear()
        binding.searchContainer.isVisible = false
        binding.pager.isVisible = true
        binding.bottomNav.isVisible = true
        applyAppBarOffset()
    }

    private fun updateToolbarTitle(position: Int) {
        supportActionBar?.title = getString(
            when (position) {
                0 -> R.string.title_popular
                1 -> R.string.title_top_rated
                else -> R.string.title_saved
            }
        )
    }

    override fun onMovieSelected(movie: Movie) {
        val details = supportFragmentManager.findFragmentById(R.id.fragment) as? DetailsFragment
        if (details == null) {
            startActivity(Intent(this, DetailsActivity::class.java).putExtra(DetailsFragment.EXTRA_MOVIE, movie))
        } else {
            details.showMovie(movie)
        }
    }

    private fun Int.toPagerIndex(): Int = when (this) {
        R.id.nav_popular -> 0
        R.id.nav_top -> 1
        else -> 2
    }

    private fun Int.toNavId(): Int = when (this) {
        0 -> R.id.nav_popular
        1 -> R.id.nav_top
        else -> R.id.nav_saved
    }

    private companion object {
        const val STATE_SEARCH_EXPANDED = "search_expanded"
    }
}
