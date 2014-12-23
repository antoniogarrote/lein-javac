# lein-javac

A Leiningen plugin to compile java sources.

The plugin allows you to compile Java files from one or more
directories of your project. The compiled class files will be written
to the `classes` directory of your project.

## Configuration

The directories containing java source files can be specified by the
`:java-source-path` option in the `project.clj` file. Compiler options
for all java source directories can be specified with the
`:java-options` option. Compiler options specific to a certain
directory can be provided after the source directory.

Example:

    (defproject sample-project "0.0.1-SNAPSHOT"
    :dependencies [[org.clojure/clojure "1.2.0-beta1"]]
    :dev-dependencies [[lein-javac "1.2.1-SNAPSHOT"]]
    :source-path "src/clojure"
    :javac-source-path [["src/java"]
                        ["test/java" :debug "true"]]
    :javac-options {:debug "true"})

## Compilation

Compiling all java source directories.

    $ lein javac

Compiling a specific java source directories.

    $ lein javac src/java

## Installation

Use Leiningen and add lein-javac to your development dependencies.

    :dev-dependencies [[lein-javac "1.2.1-SNAPSHOT"]]

## Leiningen's built-in "javac" task

Leiningen has a built in "javac" task, that was derived from
lein-javac. Unfortunately most of the lein-javac features have been
stripped out over the time :(

Use the `:javac-source-path` and `:javac-options` keywords if you want to
use lein-javac, otherwise just `:java-source-path` and `:java-options`.

## Authors

- Caspar Florian Ebeling / febeling
- Antonio Garrote / antoniogarrote
- Roman Scherer / r0man

## License

EPL license
