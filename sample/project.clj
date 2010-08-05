;; This project is used for lein-javac's test suite, so don't change
;; any of these values without updating the relevant tests. If you
;; just want a basic project to work from, generate a new one with
;; "lein new".

(defproject sample-project "0.0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.2.0-RC1"]]
  :dev-dependencies [[lein-javac "1.2.1-SNAPSHOT"]]
  :source-path "src/clojure"
  :compile-path "build/classes"
  :hooks [leiningen.hooks.javac]
  :java-source-path [["src/java"]
                     ["test/java" :destdir "build/unit/classes"]]
  :java-options {:debug "true"})
