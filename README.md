# Microconfig IntelliJ IDEA plugin

![JetBrains IntelliJ plugins](https://img.shields.io/jetbrains/plugin/d/12064-microconfig-io?style=for-the-badge)

IntelliJ IDEA plugin for Microconfig that allows navigation to `#include` and `${placeholder}` sources, 
resolve placeholders, preview components and build your configs.

For full details please check [our website](https://microconfig.io/plugin.html).

## Build and test

```shell
./gradlew test buildPlugin
```

The installable ZIP is written to `build/distributions`.

## Publish to JetBrains Marketplace

Create a personal access token in your Marketplace profile's **My Tokens**
section, using an account with permission to update Microconfig.IO. Put the token
in `token.txt` at the project root. This file is ignored by Git; leading and
trailing whitespace is removed when Gradle reads it.

```shell
./gradlew publishPlugin
```

`publishPlugin` runs the tests, builds the distribution, and uploads the version
from `build.gradle.kts` to the Stable channel of the existing
`io.microconfig.idea-plugin` listing.

To publish to a custom channel, add `-PmarketplaceChannel=eap`. Increase the
plugin version before publishing another update; Marketplace rejects duplicate
versions.

To inspect the task graph without uploading an update:

```shell
./gradlew publishPlugin --dry-run
```

See the [JetBrains publishing guide](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html).
