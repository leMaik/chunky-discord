# Discord Rich Presence Plugin for Chunky

This is a plugin for [Chunky][chunky] that uses [discord-rpc][discord-rpc] to integrate Chunky with Discord to provide _rich presence_, i.e. show your render progress below your name.

## Installation

Download the latest plugin release for your Chunky version from the [releases page](https://github.com/leMaik/chunky-discord/releases). In the Chunky Launcher, click on _Manage plugins_ and then on _Add_ and select the `.jar` file you just downloaded. Click on `Save` to store the updated configuration, then start Chunky as usual.

**Note:** The Discord integration only works if Discord is already running when starting Chunky.

## Building the plugin

In order to build this plugin, a _Client ID_ for Discord is required. To get one, you need to register in the [Discord Developer Portal][discord-developer-portal] and create an _API application_.

Put this ID into the `gradle.properties` file and then build the project using `./gradlew jar`. The [BuildConfig plugin][buildconfig] is used to inject the ID into the code at compile time.

## License

Copyright 2020 Maik Marschner (leMaik)

Permission to modify and redistribute is granted under the terms of the GNU General Public License, Version 3. See the [LICENSE][license] file for the full license.

[chunky]: https://chunky.lemaik.de/
[discord-rpc]: https://github.com/Vatuu/discord-rpc
[buildconfig]: https://github.com/mfuerstenau/gradle-buildconfig-plugin
[discord-developer-portal]: https://discord.com/developers/applications
[license]: https://github.com/leMaik/chunky-discord/blob/master/LICENSE
