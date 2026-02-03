<img src="https://i.imgur.com/PUUmGh9.png" width="64" align="left" />

# Biomes Compass

[![Java](https://img.shields.io/badge/java-25-blue.svg?logo=java)](https://www.oracle.com/java/)
[![Discord](https://img.shields.io/badge/discord-join-7289DA.svg?logo=discord)](https://ethereallabs.it/discord)

**BiomesCompass** is a native Hytale plugin that introduces a craftable compass, allowing players to locate and track specific biomes with ease via a custom UI and HUD system.

The latest downloads can be found at [https://www.curseforge.com/hytale/mods/biomes-compass](https://www.curseforge.com/hytale/mods/biomes-compass).

It is:

* **Native** - Built for the Hytale Server API using Java.
* **Visual** - Select biomes via a beautiful in-game UI and track them with a real-time **HUD Compass**.
* **Fast** - Optimized ticking system to ensure zero main-thread lag.
* **Compatible** - Works standalone or integrates with `MultipleHUD`.

## Building

BiomesCompass uses Gradle to handle dependencies and building.

### Requirements
* Java 25 JDK or newer
* Git

### Compiling from source
```sh
git clone https://github.com/Darkeox34/BiomesCompass.git
cd BiomesCompass/
./gradlew shadowJar
```

## Features
- **Biome Search UI:** Interactive menu to browse and select available biomes in the world.
- **Compass HUD:** Animated needle that points dynamically to the nearest biome of your choice.
- **Craftable Item:** Immersive integration with a custom compass item (`Biomes_Compass`).

## Contributing
#### Pull Requests
We welcome contributions! If you'd like to improve the plugin, please create a Pull Request.
See [`CONTRIBUTING.md`](CONTRIBUTING.md) for more details.

## License
BiomesCompass is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International Public License. Please see [`LICENSE`](LICENSE) for more info.