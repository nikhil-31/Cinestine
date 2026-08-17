# Graph Report - Cinestine  (2026-08-17)

## Corpus Check
- 56 files · ~20,219 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 802 nodes · 1318 edges · 56 communities (36 shown, 20 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 37 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `94873d6c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- FavouritesFragment
- MovieListViewModel
- MovieRepository
- MainActivity
- DetailsFragment
- FavouriteDao
- TmdbApi
- ReviewAdapter
- DiscoverResultsViewModel
- ThemePreferences
- PersonFragment
- Cinestine
- TmdbDtos.kt
- ExampleUnitTest
- DetailsActivity
- gradlew
- SeasonAdapter
- CastMember
- EpisodeFragment
- SearchViewModel
- DiscoverFilter
- ImagePagerAdapter
- HotViewModel
- ProviderAdapter
- DetailsViewModel
- PosterAdapter
- StillAdapter
- PersonActivity
- GenreListDto
- ImagesDto
- WatchProvidersDto
- ReviewPageDto
- VideoPageDto
- MovieListItem
- .movieDetails
- .person
- .personCredits
- CollectionFragment
- .tvCredits
- .tvDetails
- .tvSeason
- CreditsDto
- SearchAdapter
- CollectionActivity
- EpisodeActivity
- KeywordPageDto
- NestedScrollableHost
- .movieReleaseDates
- .personImages
- .searchCollections
- .searchPeople
- .tvEpisode
- RegionPreferences

## God Nodes (most connected - your core abstractions)
1. `TmdbApi` - 48 edges
2. `MovieRepository` - 41 edges
3. `Movie` - 41 edges
4. `MediaType` - 34 edges
5. `MainActivity` - 32 edges
6. `DetailsFragment` - 28 edges
7. `HotViewModel` - 21 edges
8. `MovieListViewModel` - 19 edges
9. `HotFragment` - 17 edges
10. `MovieListFragment` - 17 edges

## Surprising Connections (you probably didn't know these)
- `toMovie()` --calls--> `Movie`  [INFERRED]
  app/src/main/java/nikhil/cinestine/data/local/FavouriteEntity.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `mediaTypeFrom()` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/discover/DiscoverResultsFragment.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `FavouritesFragment` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/favourites/FavouritesFragment.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `FavouritesViewModel` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/favourites/FavouritesViewModel.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `HotFragment` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/hot/HotFragment.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt

## Import Cycles
- None detected.

## Communities (56 total, 20 thin omitted)

### Community 0 - "FavouritesFragment"
Cohesion: 0.06
Nodes (23): FavouritesFragment, RecyclerView, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View (+15 more)

### Community 1 - "MovieListViewModel"
Cohesion: 0.07
Nodes (24): MovieCategory, NOW_PLAYING, POPULAR, TOP_RATED, TRENDING, UPCOMING, Bundle, Fragment (+16 more)

### Community 2 - "MovieRepository"
Cohesion: 0.07
Nodes (25): CinestineApp, Flow, MovieRepository, CollectionSummary, EpisodeDetails, Genre, Kind, COMPANY (+17 more)

### Community 3 - "MainActivity"
Cohesion: 0.07
Nodes (16): ActivityMainBinding, DiscoverResultsActivity, AppCompatActivity, Bundle, AppCompatActivity, Bundle, MainActivity, ViewPager2 (+8 more)

### Community 4 - "DetailsFragment"
Cohesion: 0.05
Nodes (32): Episode, Trailer, VideoGroup, CLIP, TEASER, TRAILER, DetailsFragment, Bundle (+24 more)

### Community 5 - "FavouriteDao"
Cohesion: 0.14
Nodes (9): AppDatabase, migrate(), FavouriteDao, Flow, FavouriteEntity, toEntity(), toMovie(), RoomDatabase (+1 more)

### Community 6 - "TmdbApi"
Cohesion: 0.16
Nodes (3): TmdbApi, MoviePageDto, TvPageDto

### Community 7 - "ReviewAdapter"
Cohesion: 0.27
Nodes (8): Review, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, ReviewAdapter, ReviewViewHolder

### Community 8 - "DiscoverResultsViewModel"
Cohesion: 0.09
Nodes (17): DiscoverResultsFragment, RecyclerView, Bundle, Fragment, LayoutInflater, RecyclerView, View, ViewGroup (+9 more)

### Community 10 - "PersonFragment"
Cohesion: 0.08
Nodes (18): Bundle, Fragment, LayoutInflater, View, ViewGroup, PersonFragment, CreditFilter, ACTING (+10 more)

### Community 11 - "Cinestine"
Cohesion: 0.20
Nodes (9): Cinestine, @Copyright 2016 Nikhil Bhaskar, Credits, Instructions, Libraries used:, License, Overview, Playstore (+1 more)

### Community 12 - "TmdbDtos.kt"
Cohesion: 0.08
Nodes (22): AggregateCastDto, CastDto, CollectionDto, CollectionSummaryDto, CompanyDto, ContentRatingDto, ContentRatingsDto, CreditTitleDto (+14 more)

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

### Community 20 - "CastMember"
Cohesion: 0.26
Nodes (8): CastMember, areContentsTheSame(), areItemsTheSame(), CastAdapter, CastViewHolder, ListAdapter, RecyclerView, ViewGroup

### Community 21 - "EpisodeFragment"
Cohesion: 0.11
Nodes (14): EpisodeFragment, Bundle, Fragment, LayoutInflater, View, ViewGroup, EpisodeUiState, EpisodeViewModel (+6 more)

### Community 22 - "SearchViewModel"
Cohesion: 0.06
Nodes (22): SearchScope, COLLECTION, MOVIE, PERSON, TV, Bundle, Fragment, FragmentMovieListBinding (+14 more)

### Community 23 - "DiscoverFilter"
Cohesion: 0.17
Nodes (11): DiscoverFilter, DiscoverFilterFragment, filterFrom(), Bundle, LayoutInflater, View, ViewGroup, newInstance() (+3 more)

### Community 24 - "ImagePagerAdapter"
Cohesion: 0.18
Nodes (10): GalleryDialogFragment, Holder, ImagePagerAdapter, Bundle, Holder, RecyclerView, ViewGroup, newInstance() (+2 more)

### Community 25 - "HotViewModel"
Cohesion: 0.05
Nodes (32): HotFragment, RecyclerView, Bundle, Fragment, LayoutInflater, RecyclerView, View, ViewGroup (+24 more)

### Community 26 - "ProviderAdapter"
Cohesion: 0.27
Nodes (8): WatchProvider, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, ProviderAdapter, ProviderViewHolder

### Community 27 - "DetailsViewModel"
Cohesion: 0.19
Nodes (8): DetailsViewModel, Extras, Factory, Job, StateFlow, T, ViewModel, ViewModelProvider

### Community 28 - "PosterAdapter"
Cohesion: 0.39
Nodes (5): ListAdapter, RecyclerView, ViewGroup, PosterAdapter, PosterViewHolder

### Community 29 - "StillAdapter"
Cohesion: 0.39
Nodes (5): ListAdapter, RecyclerView, ViewGroup, StillAdapter, StillViewHolder

### Community 30 - "PersonActivity"
Cohesion: 0.40
Nodes (3): AppCompatActivity, Bundle, PersonActivity

### Community 36 - "MovieListItem"
Cohesion: 0.23
Nodes (8): areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, MovieAdapter, MovieListItem, MovieViewHolder

### Community 40 - "CollectionFragment"
Cohesion: 0.11
Nodes (14): CollectionFragment, Bundle, Fragment, LayoutInflater, View, ViewGroup, CollectionUiState, CollectionViewModel (+6 more)

### Community 45 - "SearchAdapter"
Cohesion: 0.24
Nodes (9): SearchHit, areContentsTheSame(), areItemsTheSame(), Holder, Holder, ListAdapter, RecyclerView, ViewGroup (+1 more)

### Community 46 - "CollectionActivity"
Cohesion: 0.40
Nodes (3): CollectionActivity, AppCompatActivity, Bundle

### Community 47 - "EpisodeActivity"
Cohesion: 0.40
Nodes (3): EpisodeActivity, AppCompatActivity, Bundle

### Community 49 - "NestedScrollableHost"
Cohesion: 0.36
Nodes (4): View, NestedScrollableHost, FrameLayout, MotionEvent

## Knowledge Gaps
- **53 isolated node(s):** `MovieDto`, `TvDto`, `VideoDto`, `ReviewDto`, `GenreDto` (+48 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **20 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movie` connect `MovieRepository` to `FavouritesFragment`, `MovieListViewModel`, `MainActivity`, `DetailsFragment`, `FavouriteDao`, `MovieListItem`, `CollectionFragment`, `DiscoverResultsViewModel`, `PersonFragment`, `SearchViewModel`, `DiscoverFilter`, `HotViewModel`, `DetailsViewModel`, `PosterAdapter`?**
  _High betweenness centrality (0.217) - this node is a cross-community bridge._
- **Why does `MediaType` connect `MovieRepository` to `FavouritesFragment`, `MovieListViewModel`, `MainActivity`, `DetailsFragment`, `SearchViewModel`, `DiscoverFilter`, `HotViewModel`?**
  _High betweenness centrality (0.147) - this node is a cross-community bridge._
- **Why does `MainActivity` connect `MainActivity` to `DetailsFragment`, `SearchViewModel`?**
  _High betweenness centrality (0.089) - this node is a cross-community bridge._
- **What connects `MovieDto`, `TvDto`, `VideoDto` to the rest of the system?**
  _53 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `FavouritesFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.0641025641025641 - nodes in this community are weakly interconnected._
- **Should `MovieListViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.06570048309178744 - nodes in this community are weakly interconnected._
- **Should `MovieRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.06672519754170325 - nodes in this community are weakly interconnected._