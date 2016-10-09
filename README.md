# lenses-codec-idea-plugin
Plugin for IDEA to support lenses-codec macros.

See [IntelliJ Scala Macros Support](https://blog.jetbrains.com/scala/2015/10/14/intellij-api-to-build-scala-macros-support/) for description of why this is needed - we provide a macro very similar to the Monocle one, but generating LensN, so we need a new plugin.

The Monocle plugin source is [here](https://github.com/JetBrains/intellij-scala/blob/idea15.x/src/org/jetbrains/plugins/scala/lang/psi/impl/toplevel/typedef/MonocleInjector.scala), and we just need to change the package names, and Lens to LensN

This project is based on the [skeleton plugin example](https://github.com/JetBrains/sbt-idea-example) with just the actual injector package/class and plugin name changed.

To install plugin, clone this repository then run `sbt`, and run task `packagePlugin`. The plugin will be produced in `target/scala-2.11` directory, and can be installed in Idea from `Preferences -> Plugins -> Install plugin from disk...`
