# github-java-trends

[![Build Status][travis-image]][travis-url]

**GitHub Java Trends** is a simples app which show the trends of java world at github using the [GiHub API](https://developer.github.com/v3/) to show a list of repositories ordered by *stars*. By clicking at the repository item you should be redirect to a *Pull Request Screen* of the repository.

## Architecture

Used Model View Presenter(MVP) suggested by google at [android-architecture](https://github.com/googlesamples/android-architecture) to facilitate separation of responsibilities and make the code more testable

## Technologies

- [Requery](https://github.com/requery/requery) for storage
- [Retrofit2](https://square.github.io/retrofit) for network
- [RxJava2](https://github.com/ReactiveX/RxJava) and [RxAndroid2](https://github.com/ReactiveX/RxAndroid) for asynchronicity
- [Gson](https://github.com/google/gson) for serialization / deserialization
- [Picasso](https://github.com/square/picasso) for image loading
- [Dagger2](https://google.github.io/dagger/) for dependency injection
- [Butter Knife](http://jakewharton.github.io/butterknife/) for view binding
- [Super Recycler View](https://github.com/Malinskiy/SuperRecyclerView) for list repositories and PRs
- [Circle Image View](https://github.com/hdodenhof/CircleImageView) for show repository owner image
- [JUnit](http://junit.org/junit4/) and [Mockito](http://site.mockito.org/) for tests


## Preview

<img src="./assets/preview.gif" alt="Preview" width="300px" height="500px">

## Images

You can see some more images about the application [here](https://github.com/carloseduardosx/github-java-trends/blob/master/IMAGES.md)

## License

github-java-trends is released under the Apache License. See [LICENSE](https://github.com/carloseduardosx/github-java-trends/blob/master/LICENSE) for details.

[travis-image]: https://travis-ci.com/carloseduardosx/github-java-trends.svg?token=cEdSDvBVFsYxaRSsqpD3&branch=master
[travis-url]: https://travis-ci.com/carloseduardosx/github-java-trends

