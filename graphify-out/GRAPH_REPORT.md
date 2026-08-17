# Graph Report - Cinestine  (2026-08-17)

## Corpus Check
- 55 files · ~19,127 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 776 nodes · 1266 edges · 52 communities (34 shown, 18 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 35 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `3b068b72`
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
- Episode
- CastMember
- SearchViewModel
- DiscoverFilter
- ImagePagerAdapter
- HotViewModel
- ProviderAdapter
- Kind
- PosterAdapter
- StillAdapter
- PersonActivity
- GenreListDto
- ImagesDto
- WatchProvidersDto
- ReviewPageDto
- VideoPageDto
- .movieDetails
- .person
- .personCredits
- MovieListItem
- .tvCredits
- .tvDetails
- .tvSeason
- SearchAdapter
- CollectionActivity
- EpisodeActivity
- KeywordPageDto
- .movieReleaseDates
- .personImages
- .searchCollections
- .searchPeople
- .tvEpisode

## God Nodes (most connected - your core abstractions)
1. `TmdbApi` - 48 edges
2. `MovieRepository` - 41 edges
3. `Movie` - 40 edges
4. `MediaType` - 34 edges
5. `MainActivity` - 32 edges
6. `DetailsFragment` - 27 edges
7. `HotViewModel` - 20 edges
8. `MovieListViewModel` - 19 edges
9. `MovieListFragment` - 17 edges
10. `SearchViewModel` - 17 edges

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

## Communities (52 total, 18 thin omitted)

### Community 0 - "FavouritesFragment"
Cohesion: 0.08
Nodes (18): FavouritesFragment, RecyclerView, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View (+10 more)

### Community 1 - "MovieListViewModel"
Cohesion: 0.06
Nodes (24): MovieCategory, NOW_PLAYING, POPULAR, TOP_RATED, TRENDING, UPCOMING, Bundle, Fragment (+16 more)

### Community 2 - "MovieRepository"
Cohesion: 0.07
Nodes (21): CinestineApp, Flow, MovieRepository, CollectionSummary, EpisodeDetails, Genre, MediaImage, MediaType (+13 more)

### Community 3 - "MainActivity"
Cohesion: 0.07
Nodes (15): ActivityMainBinding, DiscoverResultsActivity, AppCompatActivity, Bundle, AppCompatActivity, Bundle, MainActivity, MenuItem (+7 more)

### Community 4 - "DetailsFragment"
Cohesion: 0.05
Nodes (32): Trailer, VideoGroup, CLIP, TEASER, TRAILER, DetailsFragment, Bundle, Fragment (+24 more)

### Community 5 - "FavouriteDao"
Cohesion: 0.14
Nodes (9): AppDatabase, migrate(), FavouriteDao, Flow, FavouriteEntity, toEntity(), toMovie(), RoomDatabase (+1 more)

### Community 6 - "TmdbApi"
Cohesion: 0.14
Nodes (4): TmdbApi, CreditsDto, MoviePageDto, TvPageDto

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

### Community 20 - "Episode"
Cohesion: 0.27
Nodes (8): Episode, areContentsTheSame(), areItemsTheSame(), EpisodeAdapter, EpisodeViewHolder, ListAdapter, RecyclerView, ViewGroup

### Community 21 - "CastMember"
Cohesion: 0.08
Nodes (22): CastMember, areContentsTheSame(), areItemsTheSame(), CastAdapter, CastViewHolder, ListAdapter, RecyclerView, ViewGroup (+14 more)

### Community 22 - "SearchViewModel"
Cohesion: 0.06
Nodes (22): SearchScope, COLLECTION, MOVIE, PERSON, TV, Bundle, Fragment, FragmentMovieListBinding (+14 more)

### Community 23 - "DiscoverFilter"
Cohesion: 0.17
Nodes (11): DiscoverFilter, DiscoverFilterFragment, filterFrom(), Bundle, LayoutInflater, View, ViewGroup, newInstance() (+3 more)

### Community 24 - "ImagePagerAdapter"
Cohesion: 0.12
Nodes (15): GalleryDialogFragment, Holder, ImagePagerAdapter, Bundle, Holder, RecyclerView, ViewGroup, newInstance() (+7 more)

### Community 25 - "HotViewModel"
Cohesion: 0.06
Nodes (31): HotFragment, RecyclerView, Bundle, Fragment, LayoutInflater, RecyclerView, View, ViewGroup (+23 more)

### Community 26 - "ProviderAdapter"
Cohesion: 0.27
Nodes (8): WatchProvider, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, ProviderAdapter, ProviderViewHolder

### Community 27 - "Kind"
Cohesion: 0.50
Nodes (4): Kind, COMPANY, KEYWORD, NETWORK

### Community 28 - "PosterAdapter"
Cohesion: 0.29
Nodes (7): ListAdapter, RecyclerView, ViewGroup, PosterAdapter, PosterViewHolder, ImageView, TextView

### Community 29 - "StillAdapter"
Cohesion: 0.39
Nodes (5): ListAdapter, RecyclerView, ViewGroup, StillAdapter, StillViewHolder

### Community 30 - "PersonActivity"
Cohesion: 0.40
Nodes (3): AppCompatActivity, Bundle, PersonActivity

### Community 40 - "MovieListItem"
Cohesion: 0.08
Nodes (22): CollectionFragment, Bundle, Fragment, LayoutInflater, View, ViewGroup, CollectionUiState, CollectionViewModel (+14 more)

### Community 45 - "SearchAdapter"
Cohesion: 0.24
Nodes (9): SearchHit, areContentsTheSame(), areItemsTheSame(), Holder, Holder, ListAdapter, RecyclerView, ViewGroup (+1 more)

### Community 46 - "CollectionActivity"
Cohesion: 0.40
Nodes (3): CollectionActivity, AppCompatActivity, Bundle

### Community 47 - "EpisodeActivity"
Cohesion: 0.40
Nodes (3): EpisodeActivity, AppCompatActivity, Bundle

## Knowledge Gaps
- **50 isolated node(s):** `MovieDto`, `TvDto`, `VideoDto`, `ReviewDto`, `GenreDto` (+45 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **18 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movie` connect `MovieRepository` to `FavouritesFragment`, `MovieListViewModel`, `MainActivity`, `DetailsFragment`, `FavouriteDao`, `MovieListItem`, `DiscoverResultsViewModel`, `PersonFragment`, `SearchViewModel`, `DiscoverFilter`, `HotViewModel`, `PosterAdapter`?**
  _High betweenness centrality (0.232) - this node is a cross-community bridge._
- **Why does `MediaType` connect `MovieRepository` to `FavouritesFragment`, `MovieListViewModel`, `MainActivity`, `DetailsFragment`, `SearchViewModel`, `DiscoverFilter`, `HotViewModel`?**
  _High betweenness centrality (0.140) - this node is a cross-community bridge._
- **Why does `MainActivity` connect `MainActivity` to `DetailsFragment`, `SearchViewModel`?**
  _High betweenness centrality (0.093) - this node is a cross-community bridge._
- **What connects `MovieDto`, `TvDto`, `VideoDto` to the rest of the system?**
  _50 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `FavouritesFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.07526881720430108 - nodes in this community are weakly interconnected._
- **Should `MovieListViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.06382978723404255 - nodes in this community are weakly interconnected._
- **Should `MovieRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.07341269841269842 - nodes in this community are weakly interconnected._