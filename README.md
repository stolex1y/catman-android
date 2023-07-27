# Catman App

Catman - time management application for Android.

## Features

Main app features:
- assistance in planning the day, managing time, activities and concentration
- tracking progress towards achieving goals
- keeping records of time spent at work/study
- fighting procrastination, tracking deadlines

## Architecture

The Clean Architecture and UDF were used to create the application. So there are three layers: data, domain, ui.
- __Domain__ layer contains the application data model and the repository interfaces for accessing the data.
- __Data__ layer contains objects that come from the server, as well as implementations of the domain layer repositories.
- __UI__ layer contains the user interface as well as components for communicating with the domain layer.
UI components are associated with the [ViewModel][viewmodel], which are associated with repositories to receive data.
ViewModel classes have state (eg Init, Loaded, Error) and data (eg current weather).
The UI collects data and state via [StateFlow][stateflow] and renders as needed.
If some event is received from the user, for example, updating data, then this event is sent to the ViewModel instance, after which the state and data are updated, if necessary.

The app uses Room for local database storage. In addition, WorkManager is used to perform background work, so that data change requests are fulfilled, even when the application exits.
All of these components are connected by the [Hilt][hilt] dependency injection framework.
At the moment, most screens are created using the [View][view] system, including custom views.
In addition, data/view binding, services, lifecycle, livedata, navigation, recycler view, kotlin coroutines, kotlin flows were also used.

 [compose]: https://developer.android.com/jetpack/compose
 [viewmodel]: https://developer.android.com/topic/libraries/architecture/viewmodel
 [stateflow]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/
 [hilt]: https://dagger.dev/hilt/
 [view]: https://developer.android.com/reference/android/view/View
