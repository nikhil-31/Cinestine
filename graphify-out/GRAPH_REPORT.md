# Graph Report - Cinestine  (2026-08-16)

## Corpus Check
- 30 files · ~7,593 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 285 nodes · 407 edges · 23 communities (22 shown, 1 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 8 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b203f75f`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- FavouritesFragment
- MovieListFragment
- Movie
- MainActivity
- DetailsFragment
- FavouriteDao
- TmdbApi
- ReviewAdapter
- TrailerAdapter
- ThemePreferences
- MovieListViewModel
- Cinestine
- MovieAdapter
- ExampleUnitTest
- DetailsActivity
- gradlew
- FavouritesViewModel
- DetailsViewModel
- SearchFragment
- SearchViewModel

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 22 edges
2. `Movie` - 21 edges
3. `DetailsFragment` - 15 edges
4. `SearchViewModel` - 14 edges
5. `MovieRepository` - 13 edges
6. `MovieListFragment` - 11 edges
7. `MovieListViewModel` - 11 edges
8. `DetailsViewModel` - 10 edges
9. `SearchFragment` - 10 edges
10. `FavouritesFragment` - 9 edges

## Surprising Connections (you probably didn't know these)
- `toMovie()` --calls--> `Movie`  [INFERRED]
  app/src/main/java/nikhil/cinestine/data/local/FavouriteEntity.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `FavouritesViewModel` --references--> `Movie`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/favourites/FavouritesViewModel.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `areContentsTheSame()` --references--> `Trailer`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/details/TrailerAdapter.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `areItemsTheSame()` --references--> `Trailer`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/details/TrailerAdapter.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `areContentsTheSame()` --references--> `Review`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/details/ReviewAdapter.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt

## Import Cycles
- None detected.

## Communities (23 total, 1 thin omitted)

### Community 0 - "FavouritesFragment"
Cohesion: 0.11
Nodes (12): FavouritesFragment, RecyclerView, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View (+4 more)

### Community 1 - "MovieListFragment"
Cohesion: 0.12
Nodes (13): MovieCategory, POPULAR, TOP_RATED, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView (+5 more)

### Community 2 - "Movie"
Cohesion: 0.18
Nodes (6): CinestineApp, Flow, MovieRepository, Movie, Application, Parcelable

### Community 3 - "MainActivity"
Cohesion: 0.10
Nodes (10): ActivityMainBinding, AppCompatActivity, Bundle, MainActivity, ViewPager2, MenuItem, SearchView, BrowseScrollHost (+2 more)

### Community 4 - "DetailsFragment"
Cohesion: 0.16
Nodes (7): DetailsFragment, Bundle, Fragment, LayoutInflater, View, ViewGroup, FragmentDetailsBinding

### Community 5 - "FavouriteDao"
Cohesion: 0.16
Nodes (7): AppDatabase, FavouriteDao, Flow, FavouriteEntity, toEntity(), toMovie(), RoomDatabase

### Community 6 - "TmdbApi"
Cohesion: 0.19
Nodes (7): TmdbApi, MovieDto, MoviePageDto, ReviewDto, ReviewPageDto, VideoDto, VideoPageDto

### Community 7 - "ReviewAdapter"
Cohesion: 0.24
Nodes (8): Review, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, ReviewAdapter, ReviewViewHolder

### Community 8 - "TrailerAdapter"
Cohesion: 0.22
Nodes (8): Trailer, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, TrailerAdapter, TrailerViewHolder

### Community 10 - "MovieListViewModel"
Cohesion: 0.21
Nodes (7): Factory, StateFlow, T, ViewModel, ViewModelProvider, MovieListUiState, MovieListViewModel

### Community 11 - "Cinestine"
Cohesion: 0.20
Nodes (9): Cinestine, @Copyright 2016 Nikhil Bhaskar, Credits, Instructions, Libraries used:, License, Overview, Playstore (+1 more)

### Community 12 - "MovieAdapter"
Cohesion: 0.26
Nodes (8): areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, MovieAdapter, MovieListItem, MovieViewHolder

### Community 13 - "ExampleUnitTest"
Cohesion: 0.53
Nodes (3): ExampleUnitTest, RunWith, Test

### Community 14 - "DetailsActivity"
Cohesion: 0.40
Nodes (3): DetailsActivity, AppCompatActivity, Bundle

### Community 15 - "gradlew"
Cohesion: 0.60
Nodes (3): gradlew script, die(), warn()

### Community 19 - "FavouritesViewModel"
Cohesion: 0.29
Nodes (6): Factory, FavouritesViewModel, StateFlow, T, ViewModel, ViewModelProvider

### Community 20 - "DetailsViewModel"
Cohesion: 0.21
Nodes (8): DetailsUiState, DetailsViewModel, Extras, Factory, StateFlow, T, ViewModel, ViewModelProvider

### Community 21 - "SearchFragment"
Cohesion: 0.15
Nodes (9): Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View, ViewGroup, SearchFragment (+1 more)

### Community 22 - "SearchViewModel"
Cohesion: 0.16
Nodes (8): Factory, StateFlow, T, ViewModel, ViewModelProvider, SearchUiState, SearchViewModel, Job

## Knowledge Gaps
- **12 isolated node(s):** `MovieDto`, `VideoDto`, `ReviewDto`, `POPULAR`, `TOP_RATED` (+7 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **1 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movie` connect `Movie` to `FavouritesFragment`, `MovieListFragment`, `MainActivity`, `DetailsFragment`, `FavouriteDao`, `MovieListViewModel`, `MovieAdapter`, `FavouritesViewModel`, `DetailsViewModel`, `SearchFragment`, `SearchViewModel`?**
  _High betweenness centrality (0.436) - this node is a cross-community bridge._
- **Why does `MainActivity` connect `MainActivity` to `Movie`, `SearchViewModel`?**
  _High betweenness centrality (0.149) - this node is a cross-community bridge._
- **Why does `SearchFragment` connect `SearchFragment` to `MainActivity`, `SearchViewModel`?**
  _High betweenness centrality (0.091) - this node is a cross-community bridge._
- **What connects `MovieDto`, `VideoDto`, `ReviewDto` to the rest of the system?**
  _12 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `FavouritesFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.10822510822510822 - nodes in this community are weakly interconnected._
- **Should `MovieListFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.12380952380952381 - nodes in this community are weakly interconnected._
- **Should `MainActivity` be split into smaller, more focused modules?**
  _Cohesion score 0.09982174688057041 - nodes in this community are weakly interconnected._