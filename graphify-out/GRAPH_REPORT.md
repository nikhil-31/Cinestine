# Graph Report - Cinestine  (2026-08-17)

## Corpus Check
- 32 files · ~9,996 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 354 nodes · 548 edges · 23 communities (22 shown, 1 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 10 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `5a22fa66`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- FavouritesFragment
- MovieListFragment
- Movie
- MainActivity
- DetailsFragment
- FavouriteDao
- TmdbDtos.kt
- ReviewAdapter
- TrailerAdapter
- ThemePreferences
- MovieListViewModel
- Cinestine
- MovieAdapter
- ExampleUnitTest
- DetailsActivity
- gradlew
- SeasonAdapter
- Episode
- SearchFragment
- MediaType

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 28 edges
2. `Movie` - 23 edges
3. `MovieRepository` - 19 edges
4. `DetailsFragment` - 19 edges
5. `MediaType` - 18 edges
6. `SearchViewModel` - 15 edges
7. `TmdbApi` - 14 edges
8. `MovieListViewModel` - 14 edges
9. `DetailsViewModel` - 12 edges
10. `MovieListFragment` - 12 edges

## Surprising Connections (you probably didn't know these)
- `toMovie()` --calls--> `Movie`  [INFERRED]
  app/src/main/java/nikhil/cinestine/data/local/FavouriteEntity.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `MovieListFragment` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/movie/MovieListFragment.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `MovieListViewModel` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/movie/MovieListViewModel.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `FavouritesViewModel` --references--> `Movie`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/favourites/FavouritesViewModel.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `areContentsTheSame()` --references--> `Trailer`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/details/TrailerAdapter.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt

## Import Cycles
- None detected.

## Communities (23 total, 1 thin omitted)

### Community 0 - "FavouritesFragment"
Cohesion: 0.08
Nodes (18): FavouritesFragment, RecyclerView, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View (+10 more)

### Community 1 - "MovieListFragment"
Cohesion: 0.13
Nodes (13): MovieCategory, POPULAR, TOP_RATED, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView (+5 more)

### Community 2 - "Movie"
Cohesion: 0.14
Nodes (8): CinestineApp, Flow, MovieRepository, Movie, Season, TitleDetails, Application, Parcelable

### Community 3 - "MainActivity"
Cohesion: 0.09
Nodes (11): ActivityMainBinding, AppCompatActivity, Bundle, MainActivity, ViewPager2, MenuItem, SearchView, BrowseScrollHost (+3 more)

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

### Community 19 - "SeasonAdapter"
Cohesion: 0.29
Nodes (8): areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, SeasonAdapter, SeasonListItem, SeasonViewHolder

### Community 20 - "Episode"
Cohesion: 0.27
Nodes (8): Episode, areContentsTheSame(), areItemsTheSame(), EpisodeAdapter, EpisodeViewHolder, ListAdapter, RecyclerView, ViewGroup

### Community 21 - "SearchFragment"
Cohesion: 0.15
Nodes (9): Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View, ViewGroup, SearchFragment (+1 more)

### Community 22 - "MediaType"
Cohesion: 0.13
Nodes (11): MediaType, MOVIE, TV, Factory, Job, StateFlow, T, ViewModel (+3 more)

## Knowledge Gaps
- **19 isolated node(s):** `MovieDto`, `TvDto`, `VideoDto`, `ReviewDto`, `GenreDto` (+14 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **1 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movie` connect `Movie` to `FavouritesFragment`, `MovieListFragment`, `MainActivity`, `DetailsFragment`, `FavouriteDao`, `MovieListViewModel`, `MovieAdapter`, `SearchFragment`, `MediaType`?**
  _High betweenness centrality (0.331) - this node is a cross-community bridge._
- **Why does `MediaType` connect `MediaType` to `MovieListFragment`, `Movie`, `MainActivity`, `ReviewAdapter`, `TrailerAdapter`, `MovieListViewModel`?**
  _High betweenness centrality (0.128) - this node is a cross-community bridge._
- **Why does `MainActivity` connect `MainActivity` to `Movie`, `MediaType`?**
  _High betweenness centrality (0.121) - this node is a cross-community bridge._
- **What connects `MovieDto`, `TvDto`, `VideoDto` to the rest of the system?**
  _19 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `FavouritesFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.07816091954022988 - nodes in this community are weakly interconnected._
- **Should `MovieListFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.13157894736842105 - nodes in this community are weakly interconnected._
- **Should `Movie` be split into smaller, more focused modules?**
  _Cohesion score 0.1396011396011396 - nodes in this community are weakly interconnected._