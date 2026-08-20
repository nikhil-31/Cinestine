# Graph Report - Cinestine  (2026-08-20)

## Corpus Check
- 61 files · ~65,703 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 842 nodes · 1388 edges · 60 communities (40 shown, 20 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 38 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `18f87565`
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
- DetailsViewModel
- CastMember
- SearchViewModel
- DiscoverFilter
- ImagePagerAdapter
- HotViewModel
- ProviderAdapter
- Episode
- Movie
- MediaImage
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
- MovieListItem
- .tvCredits
- .tvDetails
- .tvSeason
- Trailer
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
- CreditsDto
- Intent
- .onCreateView
- VideoGroup

## God Nodes (most connected - your core abstractions)
1. `Movie` - 49 edges
2. `TmdbApi` - 48 edges
3. `MovieRepository` - 43 edges
4. `MediaType` - 34 edges
5. `MainActivity` - 33 edges
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
- `CinestineApp` --references--> `MovieRepository`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/CinestineApp.kt → app/src/main/java/nikhil/cinestine/data/MovieRepository.kt
- `mediaTypeFrom()` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/discover/DiscoverResultsFragment.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt
- `FavouritesFragment` --references--> `MediaType`  [EXTRACTED]
  app/src/main/java/nikhil/cinestine/ui/favourites/FavouritesFragment.kt → app/src/main/java/nikhil/cinestine/model/Movie.kt

## Import Cycles
- None detected.

## Communities (60 total, 20 thin omitted)

### Community 0 - "FavouritesFragment"
Cohesion: 0.09
Nodes (16): FavouritesFragment, RecyclerView, Bundle, Fragment, FragmentMovieListBinding, LayoutInflater, RecyclerView, View (+8 more)

### Community 1 - "MovieListViewModel"
Cohesion: 0.07
Nodes (24): MovieCategory, NOW_PLAYING, POPULAR, TOP_RATED, TRENDING, UPCOMING, Bundle, Fragment (+16 more)

### Community 2 - "MovieRepository"
Cohesion: 0.06
Nodes (24): Flow, MovieRepository, CollectionSummary, EpisodeDetails, Genre, Kind, COMPANY, KEYWORD (+16 more)

### Community 3 - "MainActivity"
Cohesion: 0.08
Nodes (12): ActivityMainBinding, AppCompatActivity, Bundle, MainActivity, ViewPager2, MenuItem, SearchView, BrowseScrollHost (+4 more)

### Community 4 - "DetailsFragment"
Cohesion: 0.18
Nodes (3): DetailsFragment, Fragment, FragmentDetailsBinding

### Community 5 - "FavouriteDao"
Cohesion: 0.08
Nodes (14): AppDatabase, migrate(), FavouriteDao, Flow, FavouriteEntity, toEntity(), toMovie(), Flow (+6 more)

### Community 6 - "TmdbApi"
Cohesion: 0.16
Nodes (3): TmdbApi, MoviePageDto, TvPageDto

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
Cohesion: 0.29
Nodes (6): Architecture, Attribution, Cinestine, Features, Getting started, License

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

### Community 20 - "DetailsViewModel"
Cohesion: 0.16
Nodes (9): DetailsUiState, DetailsViewModel, Extras, Factory, Job, StateFlow, T, ViewModel (+1 more)

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
Cohesion: 0.18
Nodes (10): GalleryDialogFragment, Holder, ImagePagerAdapter, Bundle, Holder, RecyclerView, ViewGroup, newInstance() (+2 more)

### Community 25 - "HotViewModel"
Cohesion: 0.05
Nodes (32): HotFragment, RecyclerView, Bundle, Fragment, LayoutInflater, RecyclerView, View, ViewGroup (+24 more)

### Community 26 - "ProviderAdapter"
Cohesion: 0.27
Nodes (8): WatchProvider, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, ProviderAdapter, ProviderViewHolder

### Community 27 - "Episode"
Cohesion: 0.22
Nodes (8): Episode, areContentsTheSame(), areItemsTheSame(), EpisodeAdapter, EpisodeViewHolder, ListAdapter, RecyclerView, ViewGroup

### Community 28 - "Movie"
Cohesion: 0.08
Nodes (16): AppAnalytics, Bundle, CinestineApp, Movie, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView (+8 more)

### Community 29 - "MediaImage"
Cohesion: 0.27
Nodes (8): MediaImage, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, StillAdapter, StillViewHolder

### Community 30 - "PersonActivity"
Cohesion: 0.40
Nodes (3): AppCompatActivity, Bundle, PersonActivity

### Community 36 - "Overlay"
Cohesion: 0.18
Nodes (9): Activity, Animator, MotionEvent, View, Overlay, AnimatorListenerAdapter, SaveConfetti, Spark (+1 more)

### Community 40 - "MovieListItem"
Cohesion: 0.08
Nodes (22): CollectionFragment, Bundle, Fragment, LayoutInflater, View, ViewGroup, CollectionUiState, CollectionViewModel (+14 more)

### Community 44 - "Trailer"
Cohesion: 0.24
Nodes (8): Trailer, areContentsTheSame(), areItemsTheSame(), ListAdapter, RecyclerView, ViewGroup, TrailerAdapter, TrailerViewHolder

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
Nodes (4): MotionEvent, View, NestedScrollableHost, FrameLayout

### Community 57 - "Intent"
Cohesion: 0.32
Nodes (4): categoryFrom(), filterFrom(), mediaTypeFrom(), Intent

### Community 58 - ".onCreateView"
Cohesion: 0.40
Nodes (4): Bundle, LayoutInflater, View, ViewGroup

### Community 59 - "VideoGroup"
Cohesion: 0.50
Nodes (4): VideoGroup, CLIP, TEASER, TRAILER

## Knowledge Gaps
- **51 isolated node(s):** `MovieDto`, `TvDto`, `VideoDto`, `ReviewDto`, `GenreDto` (+46 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **20 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Movie` connect `Movie` to `FavouritesFragment`, `MovieListViewModel`, `MovieRepository`, `DetailsFragment`, `FavouriteDao`, `MovieListItem`, `DiscoverResultsViewModel`, `PersonFragment`, `DetailsViewModel`, `SearchViewModel`, `DiscoverFilter`, `Intent`, `HotViewModel`?**
  _High betweenness centrality (0.245) - this node is a cross-community bridge._
- **Why does `MediaType` connect `MovieRepository` to `FavouritesFragment`, `MovieListViewModel`, `MainActivity`, `ReviewAdapter`, `Trailer`, `SearchViewModel`, `DiscoverFilter`, `Intent`, `HotViewModel`?**
  _High betweenness centrality (0.134) - this node is a cross-community bridge._
- **Why does `MainActivity` connect `MainActivity` to `Intent`, `Movie`, `SearchViewModel`?**
  _High betweenness centrality (0.087) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `Movie` (e.g. with `toMovie()` and `toMovie()`) actually correct?**
  _`Movie` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `MovieDto`, `TvDto`, `VideoDto` to the rest of the system?**
  _51 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `FavouritesFragment` be split into smaller, more focused modules?**
  _Cohesion score 0.08620689655172414 - nodes in this community are weakly interconnected._
- **Should `MovieListViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.06767676767676768 - nodes in this community are weakly interconnected._