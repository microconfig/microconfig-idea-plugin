# microconfig-idea-plugin

[![Build Status](https://travis-ci.com/microconfig/microconfig-idea-plugin.svg?token=dCuoGmvZ5nm4s7v5vo7S&branch=master)](https://travis-ci.com/microconfig/microconfig-idea-plugin)

Intellij Idea plugin for Microconfig that allows navigating to #include and placeholders' sources, resolving placeholders and other useful stuff.


For dev mode: 

Import as gradle project<br>
`run gradlew :intellij:runIde`

### Installation
Install plugin by name `Microconfig.io` from Idea `Settings -> Plugins -> Marketplace`

Or download plugin zip file from https://plugins.jetbrains.com/plugin/12064-microconfig-io and import it via <br> `Settings -> Plugins -> Install plugin from disk...`

### Plugin usage
Plugin adds it's submenu in `Main Menu -> Tools -> Microconfig`. Also same menu appears in `RightClick` menu.

Microconfig has two actions:
- Jump to component (F10)
- Resolve placeholder (Ctrl-F10 / Command-F10)

#### Jump to component

`Jump to component` supports navigation from `#inlude component` line. Once chosen from menu or on hotkey press 
it will try to find `component` in components directory and open corresponding file inside. 

Also it can jump to component referenced in a placeholder like `${component@key}`. To choose a placeholder just place your cursor inside `{}`. 
On jump it will try to find `component` file and then line with `key` inside it. Once found it will open that file and place cursor on `key` line.

#### Resolve placeholder

`Resolve placeholder` supports two modes. 

If activated with cursor inside `{}` it will try to resolve what value is referenced 
and if found will display it is small hint. 

If activated with cursor on a key name `url=http://${foo@host}:${foo:port}` it will try to resolve whole value with
one or more placeholders. As a result you will get whole key=value line for example `url=http://localhost:8080`.

If this you have multiple placeholder key values for different environments they all will be shown.

Due to how `Hints` work in Idea any mouse move will hide the hint. Better use hotkeys for this action.

### Config Build
You can create standard IDEA Run configuration for Microconfig to build configs with plugin (shift + F10).
