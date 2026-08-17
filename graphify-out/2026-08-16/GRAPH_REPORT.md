# Graph Report - Cinestine  (2026-08-16)

## Corpus Check
- 30 files · ~8,369 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 303 nodes · 450 edges · 21 communities (20 shown, 1 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 8 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `5a22fa66`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- FavouritesFragment
- .onCreateView
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
- SearchFragment
- SearchViewModel

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 25 edges
2. `Movie` - 22 edges
3. `DetailsFragment` - 15 edges
4. `SearchViewModel` - 15 edges
5. `MovieRepository` - 14 edges
6. `MovieListViewModel` - 14 edges
7. `MediaType` - 13 edges
8. `TmdbApi` - 11 edges
9. `MovieListFragment` - 11 edges
10. `DetailsViewModel` - 10 edges

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

## Communities (21 total, 1 thin omitted)

### Community 0 - "FavouritesFragment"
Cohesion: 0.08
Nodes (18): FavouritesFragment, RecyclerView, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View (+10 more)

### Community 1 - ".onCreateView"
Cohesion: 0.27
Nodes (6): Bundle, LayoutInflater, RecyclerView, View, ViewGroup, RecyclerView

### Community 2 - "Movie"
Cohesion: 0.11
Nodes (13): CinestineApp, Flow, MovieRepository, MediaType, MOVIE, TV, Movie, Fragment (+5 more)

### Community 3 - "MainActivity"
Cohesion: 0.10
Nodes (10): ActivityMainBinding, AppCompatActivity, Bundle, MainActivity, ViewPager2, MenuItem, SearchView, BrowseScrollHost (+2 more)

### Community 4 - "DetailsFragment"
Cohesion: 0.09
Nodes (15): DetailsFragment, Bundle, Fragment, LayoutInflater, View, ViewGroup, DetailsUiState, DetailsViewModel (+7 more)

### Community 5 - "FavouriteDao"
Cohesion: 0.14
Nodes (9): AppDatabase, migrate(), FavouriteDao, Flow, FavouriteEntity, toEntity(), toMovie(), RoomDatabase (+1 more)

### Community 6 - "TmdbApi"
Cohesion: 0.14
Nodes (9): TmdbApi, MovieDto, MoviePageDto, ReviewDto, ReviewPageDto, TvDto, TvPageDto, VideoDto (+1 more)

### Community 7 - "ReviewAdapter"
Cohesion: 0.24
Nodes (8): Review, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, ReviewAdapter, ReviewViewHolder

### Community 8 - "TrailerAdapter"
Cohesion: 0.22
Nodes (8): Trailer, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, TrailerAdapter, TrailerViewHolder

### Community 10 - "MovieListViewModel"
Cohesion: 0.14
Nodes (11): MovieCategory, POPULAR, TOP_RATED, Factory, Job, StateFlow, T, ViewModel (+3 more)

### Community 11 - "Cinestine"
Cohesion: 0.20
Nodes (9): Cinestine, @Copyright 2016 Nikhil Bhaskar, Credits, Instructions, Libraries used:, License, Overview, Playstore (+1 more)

### Community 12 - "MovieAdapter"
Cohesion: 0.29
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

### Community 21 - "SearchFragment"
Cohesion: 0.15
Nodes (9): Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View, ViewGroup, SearchFragment (+1 more)

### Community 22 - "SearchViewModel"
Cohesion: 0.16
Nodes (8): Factory, Job, StateFlow, T, ViewModel, ViewModelProvider, SearchUiState, SearchViewModel

## Knowledge Gaps
- **15 isolated node(s):** `MovieDto`, `TvDto`, `VideoDto`, `ReviewDto`, `MOVIE` (+10 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **1 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movie` connect `Movie` to `FavouritesFragment`, `MainActivity`, `DetailsFragment`, `FavouriteDao`, `MovieListViewModel`, `SearchFragment`, `SearchViewModel`?**
  _High betweenness centrality (0.346) - this node is a cross-community bridge._
- **Why does `MainActivity` connect `MainActivity` to `Movie`, `SearchViewModel`?**
  _High betweenness centrality (0.129) - this node is a cross-community bridge._
- **Why does `MediaType` connect `Movie` to `MainActivity`, `ReviewAdapter`, `TrailerAdapter`, `MovieListViewModel`, `SearchViewModel`?**
  _High betweenness centrality (0.108) - this node is a cross-community bridge._
- **What connects `MovieDto`, `TvDto`, `VideoDto` to the rest of the system?**
  _15 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `FavouritesFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.07816091954022988 - nodes in this community are weakly interconnected._
- **Should `Movie` be split into smaller, more focused modules?**
  _Cohesion score 0.10752688172043011 - nodes in this community are weakly interconnected._
- **Should `MainActivity` be split into smaller, more focused modules?**
  _Cohesion score 0.10158730158730159 - nodes in this community are weakly interconnected._