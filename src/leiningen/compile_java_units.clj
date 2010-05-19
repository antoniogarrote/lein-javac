(ns leiningen.compile-java-units
  (:use leiningen.compile-java))

(defn compile-java-units [project & params]
  (compile-testsuite project :unit))
