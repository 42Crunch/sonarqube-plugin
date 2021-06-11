# SonarQube Plugin: 42Crunch REST API Static Security Testing

## Build

This plugin can be built in two configuration, one includes built-in JSON and YAML language plugins and the other does not.

If you want to use third party plugins for YAML and JSON analysis, you have to use the build which does not include built-in JSON and YAML plugins.

By default the version with JSON and YAML languages is built. To build plugin which does not include these build it with `mvn package -Dxliic.settings.languages=openapi`
