# Graph Report - Cinestine  (2026-08-17)

## Corpus Check
- 32 files · ~10,172 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 359 nodes · 562 edges · 23 communities (22 shown, 1 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 10 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `713ebe9a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- FavouritesFragment
- MovieListFragment
- MediaType
- MainActivity
- DetailsFragment
- FavouriteDao
- TmdbDtos.kt
- ReviewAdapter
- TrailerAdapter
- ThemePreferences
- MovieListViewModel
- Cinestine
- FavouritesViewModel
- ExampleUnitTest
- DetailsActivity
- gradlew
- SeasonAdapter
- Episode
- SearchFragment
- SearchViewModel

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 30 edges
2. `MediaType` - 24 edges
3. `Movie` - 23 edges
4. `MovieRepository` - 19 edges
5. `DetailsFragment` - 19 edges
6. `SearchViewModel` - 15 edges
7. `TmdbApi` - 14 edges
8. `MovieListViewModel` - 14 edges
9. `DetailsViewModel` - 12 edges
10. `MovieListFragment` - 12 edges

## Surprising Connections (you probably didn't know these)
- `toMovie()` --calls--> `Movie`  [INFERRED]
  app/src/main/java/nikhil/cinestine/data/local/FavouriteEntity.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `FavouritesFragment` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/favourites/FavouritesFragment.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `FavouritesViewModel` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/favourites/FavouritesViewModel.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `MovieListFragment` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/movie/MovieListFragment.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `MovieListViewModel` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/movie/MovieListViewModel.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt

## Import Cycles
- None detected.

## Communities (23 total, 1 thin omitted)

### Community 0 - "FavouritesFragment"
Cohesion: 0.11
Nodes (12): FavouritesFragment, RecyclerView, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View (+4 more)

### Community 1 - "MovieListFragment"
Cohesion: 0.10
Nodes (18): areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, MovieAdapter, MovieListItem, MovieViewHolder (+10 more)

### Community 2 - "MediaType"
Cohesion: 0.11
Nodes (14): CinestineApp, Flow, MovieRepository, MediaType, MOVIE, TV, Movie, MovieCategory (+6 more)

### Community 3 - "MainActivity"
Cohesion: 0.09
Nodes (12): ActivityMainBinding, AppCompatActivity, Bundle, MainActivity, ViewPager2, MenuItem, SearchView, BrowseScrollHost (+4 more)

### Community 4 - "DetailsFragment"
Cohesion: 0.08
Nodes (16): DetailsFragment, Bundle, Fragment, LayoutInflater, View, ViewGroup, DetailsUiState, DetailsViewModel (+8 more)

### Community 5 - "FavouriteDao"
Cohesion: 0.14
Nodes (9): AppDatabase, migrate(), FavouriteDao, Flow, FavouriteEntity, toEntity(), toMovie(), RoomDatabase (+1 more)

### Community 6 - "TmdbDtos.kt"
Cohesion: 0.09
Nodes (16): TmdbApi, GenreDto, MovieDetailsDto, MovieDto, MoviePageDto, NetworkDto, ReviewDto, ReviewPageDto (+8 more)

### Community 7 - "ReviewAdapter"
Cohesion: 0.24
Nodes (8): Review, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, ReviewAdapter, ReviewViewHolder

### Community 8 - "TrailerAdapter"
Cohesion: 0.22
Nodes (8): Trailer, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, TrailerAdapter, TrailerViewHolder

### Community 10 - "MovieListViewModel"
Cohesion: 0.18
Nodes (8): Factory, Job, StateFlow, T, ViewModel, ViewModelProvider, MovieListUiState, MovieListViewModel

### Community 11 - "Cinestine"
Cohesion: 0.20
Nodes (9): Cinestine, @Copyright 2016 Nikhil Bhaskar, Credits, Instructions, Libraries used:, License, Overview, Playstore (+1 more)

### Community 12 - "FavouritesViewModel"
Cohesion: 0.25
Nodes (6): Factory, FavouritesViewModel, StateFlow, T, ViewModel, ViewModelProvider

### Community 13 - "ExampleUnitTest"
Cohesion: 0.53
Nodes (3): ExampleUnitTest, RunWith, Test

### Community 14 - "DetailsActivity"
Cohesion: 0.40
Nodes (3): DetailsActivity, AppCompatActivity, Bundle

### Community 15 - "gradlew"
Cohesion: 0.60
Nodes (3): gradlew script, die(), warn()

### Community 19 - "SeasonAdapter"
Cohesion: 0.29
Nodes (8): areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, SeasonAdapter, SeasonListItem, SeasonViewHolder

### Community 20 - "Episode"
Cohesion: 0.27
Nodes (8): Episode, areContentsTheSame(), areItemsTheSame(), EpisodeAdapter, EpisodeViewHolder, ListAdapter, RecyclerView, ViewGroup

### Community 21 - "SearchFragment"
Cohesion: 0.14
Nodes (9): Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View, ViewGroup, SearchFragment (+1 more)

### Community 22 - "SearchViewModel"
Cohesion: 0.15
Nodes (8): Factory, Job, StateFlow, T, ViewModel, ViewModelProvider, SearchUiState, SearchViewModel

## Knowledge Gaps
- **19 isolated node(s):** `MovieDto`, `TvDto`, `VideoDto`, `ReviewDto`, `GenreDto` (+14 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **1 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movie` connect `MediaType` to `FavouritesFragment`, `MovieListFragment`, `MainActivity`, `DetailsFragment`, `FavouriteDao`, `MovieListViewModel`, `FavouritesViewModel`, `SearchFragment`, `SearchViewModel`?**
  _High betweenness centrality (0.272) - this node is a cross-community bridge._
- **Why does `MediaType` connect `MediaType` to `FavouritesFragment`, `MovieListFragment`, `MainActivity`, `ReviewAdapter`, `TrailerAdapter`, `MovieListViewModel`, `FavouritesViewModel`, `SearchFragment`, `SearchViewModel`?**
  _High betweenness centrality (0.206) - this node is a cross-community bridge._
- **Why does `MainActivity` connect `MainActivity` to `MediaType`, `SearchViewModel`?**
  _High betweenness centrality (0.118) - this node is a cross-community bridge._
- **What connects `MovieDto`, `TvDto`, `VideoDto` to the rest of the system?**
  _19 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `FavouritesFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.10822510822510822 - nodes in this community are weakly interconnected._
- **Should `MovieListFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.09885057471264368 - nodes in this community are weakly interconnected._
- **Should `MediaType` be split into smaller, more focused modules?**
  _Cohesion score 0.11363636363636363 - nodes in this community are weakly interconnected._