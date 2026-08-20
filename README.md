# Cinestine

Cinestine is an Android app for discovering movies and TV shows. Browse trending and popular titles, search people and collections, see where something is streaming, and keep a local watchlist.

<a href="https://play.google.com/store/apps/details?id=comnikhil_31.httpsgithub.cinestine&hl=en&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" width="200"/></a>

Catalog data comes from [The Movie Database (TMDB)](https://www.themoviedb.org/). Cinestine is not endorsed or certified by TMDB.

## Features

- **Home and catalog** — Recently viewed, trending, popular, top rated, in theatres, upcoming, and airing-now rails, with movie and TV filters
- **Discover** — Narrow results by genre, year, and minimum score; follow keywords, studios, and networks from a title
- **Search** — Movies, TV shows, people, and collections
- **Title details** — Story, trailers, teasers, clips, reviews, cast, photos, similar titles, and region-aware “where to watch”
- **TV** — Seasons, episodes, guest stars, and next/latest episode
- **People and collections** — Filmography and collection parts
- **Watchlist** — Save titles locally, search the list, and sort by recent, rating, or title
- **Preferences** — Light, dark, or system theme, plus a watch region for certifications and streaming providers
- **Share** — Send a TMDB link for any title

## Requirements

- Android Studio with JDK 17
- Android 7.0 (API 24) or later
- A [TMDB API key](https://developer.themoviedb.org/docs/getting-started)

## Getting started

1. Clone the repository and open it in Android Studio.
2. Request a TMDB API key, then add it to `local.properties` (this file is gitignored):

   ```properties
   sdk.dir=/path/to/Android/sdk
   tmdb.api.key=YOUR_TMDB_API_KEY
   ```

3. Sync Gradle and run the `debug` variant on a device or emulator.

The TMDB key is injected into `BuildConfig` at compile time. Do not commit `local.properties`.

Firebase Analytics and Crashlytics are enabled through `app/google-services.json`. Debug HTTP logging is on only in debug builds, and the API key is never written to logcat.

## Architecture

The app follows MVVM with a single repository as the data boundary.

| Layer | Role |
| --- | --- |
| UI | Activities and fragments with View Binding, `ViewModel`, and `StateFlow` |
| Domain models | Immutable Kotlin types for titles, people, search hits, and filters |
| Data | `MovieRepository` over Retrofit (TMDB) and Room (watchlist and recently viewed) |

Main navigation is a four-tab pager: Home, Hot, Top rated, and Watchlist. Details, person, collection, episode, and discover each have their own activity.

```
app/src/main/java/nikhil/cinestine/
├── CinestineApp.kt          # App graph: networking, Room, analytics
├── analytics/               # Firebase Analytics events
├── data/                    # Repository, TMDB API, Room
├── model/                   # Shared models
└── ui/                      # Screens by feature
```

## Tech stack

| | |
| --- | --- |
| Language | Kotlin 2.2 |
| UI | Material 3, View Binding, ViewPager2, RecyclerView |
| Async | Coroutines, Flow |
| Networking | Retrofit, OkHttp, kotlinx.serialization |
| Images | Coil |
| Persistence | Room |
| Analytics | Firebase Analytics and Crashlytics |
| Build | Android Gradle Plugin 9.2, compile/target SDK 36 |

## Attribution

This product uses the TMDB API but is not endorsed or certified by [TMDB](https://www.themoviedb.org/).

## License

```
Copyright 2016–2026 Nikhil Bhaskar

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
