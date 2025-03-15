<div align="center">
  <h3>Generators 🌱</h3>
  <p>Lightweight, performant, and customizable generators for PaperMC</p>
  <img alt="License: MLP 2.0" src="https://img.shields.io/badge/license-MPL%202.0-844cfc">
  <img alt="Uses Kotlin" src="https://img.shields.io/badge/Uses-Kotlin-844cfc?logo=Kotlin">
  <img alt="Open Issues" src="https://img.shields.io/github/issues/lilysoftpaws/lily-generators?label=Issues">
</div>

### Highlights

- Lightweight and performant (can support upwards of 100,000 generators)
- Highly customizable
- Internationalization support
- ~~Easy to use API (see [API.md](API.md))~~

### Installation

> [!WARNING]
> This plugin is in development and may have bugs, though it’s been tested and should work reliably.

Just download the latest release from the [releases page](https://github.com/lilysoftpaws/lily-generators/releases) and place the jar file in the `plugins` folder of your [Paper](https://papermc.io/) server.

### Configuration

On the first run, the plugin will create a `config.yaml` file in the `plugins/generators` directory. You can freely edit this file to customize the plugin's settings.

<!-- todo: add config.yaml example -->

### Building

To build the project, you will need to have [Gradle](https://gradle.org/) installed. Once you have Gradle installed, run the following command in the project directory:

```shell
./gradlew build
```

This will build the project and generate a jar file in the `build/libs` directory.

### License

This project is licensed under the [Mozilla Public License 2.0](https://www.mozilla.org/en-US/MPL/2.0/). See [LICENSE](LICENSE) for more information.