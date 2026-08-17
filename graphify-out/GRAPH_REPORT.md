# Graph Report - Cinestine  (2026-08-17)

## Corpus Check
- 59 files · ~21,192 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 834 nodes · 1367 edges · 60 communities (41 shown, 19 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 38 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `680429ad`
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
- DiscoverFilterFragment
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
- Overlay
- .movieDetails
- .person
- .personCredits
- CollectionFragment
- .tvCredits
- .tvDetails
- .tvSeason
- Trailer
- SearchAdapter
- CollectionActivity
- EpisodeActivity
- KeywordPageDto
- Episode
- .movieReleaseDates
- .personImages
- .searchCollections
- .searchPeople
- .tvEpisode
- RegionPreferences
- .onViewCreated
- TrailerAdapter
- Intent
- Kind

## God Nodes (most connected - your core abstractions)
1. `TmdbApi` - 48 edges
2. `Movie` - 45 edges
3. `MovieRepository` - 43 edges
4. `MediaType` - 34 edges
5. `MainActivity` - 32 edges
6. `DetailsFragment` - 28 edges
7. `HotViewModel` - 22 edges
8. `MovieListViewModel` - 19 edges
9. `HotFragment` - 17 edges
10. `MovieListFragment` - 17 edges

## Surprising Connections (you probably didn't know these)
- `toMovie()` --calls--> `Movie`  [INFERRED]
  app/src/main/java/nikhil/cinestine/data/local/FavouriteEntity.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `toMovie()` --calls--> `Movie`  [INFERRED]
  app/src/main/java/nikhil/cinestine/data/local/RecentEntity.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `areContentsTheSame()` --references--> `Movie`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/details/PosterAdapter.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `areItemsTheSame()` --references--> `Movie`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/details/PosterAdapter.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `areContentsTheSame()` --references--> `MediaImage`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/details/StillAdapter.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt

## Import Cycles
- None detected.

## Communities (60 total, 19 thin omitted)

### Community 0 - "FavouritesFragment"
Cohesion: 0.06
Nodes (23): FavouritesFragment, RecyclerView, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View (+15 more)

### Community 1 - "MovieListFragment"
Cohesion: 0.08
Nodes (24): MovieCategory, NOW_PLAYING, POPULAR, TOP_RATED, TRENDING, UPCOMING, areContentsTheSame(), areItemsTheSame() (+16 more)

### Community 2 - "Movie"
Cohesion: 0.06
Nodes (27): CinestineApp, Flow, MovieRepository, CollectionSummary, DiscoverFilter, EpisodeDetails, Genre, MediaImage (+19 more)

### Community 3 - "MainActivity"
Cohesion: 0.07
Nodes (15): ActivityMainBinding, DiscoverResultsActivity, AppCompatActivity, Bundle, AppCompatActivity, Bundle, MainActivity, MenuItem (+7 more)

### Community 4 - "DetailsFragment"
Cohesion: 0.18
Nodes (4): TaggedLink, DetailsFragment, Fragment, FragmentDetailsBinding

### Community 5 - "FavouriteDao"
Cohesion: 0.08
Nodes (14): AppDatabase, migrate(), FavouriteDao, Flow, FavouriteEntity, toEntity(), toMovie(), Flow (+6 more)

### Community 6 - "TmdbApi"
Cohesion: 0.14
Nodes (4): TmdbApi, CreditsDto, MoviePageDto, TvPageDto

### Community 7 - "ReviewAdapter"
Cohesion: 0.24
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

### Community 23 - "DiscoverFilterFragment"
Cohesion: 0.18
Nodes (8): DiscoverFilterFragment, filterFrom(), Bundle, LayoutInflater, View, ViewGroup, BottomSheetDialogFragment, FragmentDiscoverFilterBinding

### Community 24 - "ImagePagerAdapter"
Cohesion: 0.12
Nodes (15): GalleryDialogFragment, Holder, ImagePagerAdapter, Bundle, Holder, RecyclerView, ViewGroup, newInstance() (+7 more)

### Community 25 - "HotViewModel"
Cohesion: 0.05
Nodes (32): HotFragment, RecyclerView, Bundle, Fragment, LayoutInflater, RecyclerView, View, ViewGroup (+24 more)

### Community 26 - "ProviderAdapter"
Cohesion: 0.27
Nodes (8): WatchProvider, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, ProviderAdapter, ProviderViewHolder

### Community 27 - "DetailsViewModel"
Cohesion: 0.17
Nodes (9): DetailsUiState, DetailsViewModel, Extras, Factory, Job, StateFlow, T, ViewModel (+1 more)

### Community 28 - "PosterAdapter"
Cohesion: 0.25
Nodes (7): areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, PosterAdapter, PosterViewHolder

### Community 29 - "StillAdapter"
Cohesion: 0.25
Nodes (7): areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, StillAdapter, StillViewHolder

### Community 30 - "PersonActivity"
Cohesion: 0.40
Nodes (3): AppCompatActivity, Bundle, PersonActivity

### Community 36 - "Overlay"
Cohesion: 0.18
Nodes (9): Activity, Animator, MotionEvent, View, Overlay, AnimatorListenerAdapter, SaveConfetti, Spark (+1 more)

### Community 40 - "CollectionFragment"
Cohesion: 0.11
Nodes (14): CollectionFragment, Bundle, Fragment, LayoutInflater, View, ViewGroup, CollectionUiState, CollectionViewModel (+6 more)

### Community 44 - "Trailer"
Cohesion: 0.20
Nodes (7): Trailer, VideoGroup, CLIP, TEASER, TRAILER, areContentsTheSame(), areItemsTheSame()

### Community 45 - "SearchAdapter"
Cohesion: 0.24
Nodes (9): SearchHit, areContentsTheSame(), areItemsTheSame(), Holder, Holder, ListAdapter, RecyclerView, ViewGroup (+1 more)

### Community 46 - "CollectionActivity"
Cohesion: 0.40
Nodes (3): CollectionActivity, AppCompatActivity, Bundle

### Community 47 - "EpisodeActivity"
Cohesion: 0.40
Nodes (3): EpisodeActivity, AppCompatActivity, Bundle

### Community 49 - "Episode"
Cohesion: 0.24
Nodes (8): Episode, areContentsTheSame(), areItemsTheSame(), EpisodeAdapter, EpisodeViewHolder, ListAdapter, RecyclerView, ViewGroup

### Community 56 - ".onViewCreated"
Cohesion: 0.22
Nodes (4): Bundle, LayoutInflater, View, ViewGroup

### Community 57 - "TrailerAdapter"
Cohesion: 0.39
Nodes (5): ListAdapter, RecyclerView, ViewGroup, TrailerAdapter, TrailerViewHolder

### Community 58 - "Intent"
Cohesion: 0.38
Nodes (4): categoryFrom(), filterFrom(), mediaTypeFrom(), Intent

### Community 59 - "Kind"
Cohesion: 0.50
Nodes (4): Kind, COMPANY, KEYWORD, NETWORK

## Knowledge Gaps
- **53 isolated node(s):** `MovieDto`, `TvDto`, `VideoDto`, `ReviewDto`, `GenreDto` (+48 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **19 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movie` connect `Movie` to `FavouritesFragment`, `MovieListFragment`, `MainActivity`, `DetailsFragment`, `FavouriteDao`, `CollectionFragment`, `DiscoverResultsViewModel`, `PersonFragment`, `SearchViewModel`, `.onViewCreated`, `HotViewModel`, `Intent`, `DetailsViewModel`, `PosterAdapter`?**
  _High betweenness centrality (0.232) - this node is a cross-community bridge._
- **Why does `MediaType` connect `Movie` to `FavouritesFragment`, `MovieListFragment`, `MainActivity`, `DetailsFragment`, `ReviewAdapter`, `Trailer`, `SearchViewModel`, `HotViewModel`, `Intent`?**
  _High betweenness centrality (0.136) - this node is a cross-community bridge._
- **Why does `MainActivity` connect `MainActivity` to `Intent`, `SearchViewModel`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `Movie` (e.g. with `toMovie()` and `toMovie()`) actually correct?**
  _`Movie` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `MovieDto`, `TvDto`, `VideoDto` to the rest of the system?**
  _53 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `FavouritesFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.0641025641025641 - nodes in this community are weakly interconnected._
- **Should `MovieListFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.07505285412262157 - nodes in this community are weakly interconnected._