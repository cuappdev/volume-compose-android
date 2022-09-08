# Volume

<p align="center"><img src=https://user-images.githubusercontent.com/60499584/109596393-25c91780-7ae4-11eb-86da-89e3bb5d72b4.png?content-type=image%2Fpng width=210/></p>

Volume is one of the latest applications by [Cornell AppDev](http://cornellappdev.com), an engineering project team at Cornell University focused on mobile app development. Volume aims to amplify the voice of student publications, helping them reach a broader audience.

## Libraries and External Services

- [Google Firebase](https://firebase.google.com/docs/android/setup): analytics and Google services
- [OkHttp](https://github.com/square/okhttp): HTTP client for networking
- [Accompanist](https://github.com/google/accompanist): A collection of extension libraries for Jetpack Compose
- [Apollo](https://github.com/apollographql/apollo-kotlin): A strongly-typed, caching GraphQL client for the JVM, Android, and Kotlin multiplatform.

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:cuappdev/volume-compose-android.git
```

Download the ```secrets.properties``` and ```google-services.json``` files. Place ```secrets.properties``` in ```volume-compose-android```, and ```google-services.json``` in ```volume-compose-android/app```.

## Directory Structure

```
volume-compose-android
+---data
    +---models
    +---repositories
+---navigation
+---ui
    +---components
    +---screens
    +---theme
    +---viewmodels
+---util
```

- Activities are located directly under `volume-compose-android`
- `models` contain the data models for communicating with the backend
- `repositories` contain the repositories that interact with the data store
- `navigation` contains the main NavHost of Volume
- `ui` contains UI layer elements
- `utils` contains helper files and methods that don't fall into any of the previous categories

## Generating signed APK
From Android Studio:
1. ***Build*** menu
2. ***Generate Signed APK...***
3. Fill in the keystore information *(you only need to do this once manually and then let Android Studio remember it)*

## Contributing

If you want to help contribute or improve Volume, feel free to submit any issues or pull requests.

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Run the linter: https://developer.android.com/studio/write/lint
5. Push your branch (git push origin my-new-feature)
6. Create a new Pull Request

## Made by Cornell AppDev

Cornell AppDev is an engineering project team at Cornell University dedicated to designing and developing mobile applications. We were founded in 2014 and have since released apps for Cornell and beyond, like [Eatery](https://play.google.com/store/apps/details?id=com.cornellappdev.android.eatery&gl=US). Our goal is to produce apps that benefit the Cornell community and the local Ithaca area as well as promote open-source development with the community. We have a diverse team of software engineers and product designers that collaborate to create apps from an idea to a reality. Cornell AppDev also aims to foster innovation and learning through training courses, campus initiatives, and collaborative research and development. For more information, visit our [website](http://www.cornellappdev.com/).

