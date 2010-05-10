(ns leiningen.compile-java-tests
  (:require lancet)
  (:use [leiningen compile compile-java])
  (:refer-clojure :exclude [compile]))

(defn compile-java-tests [project]
  (let [project-root (:root project)]
    (lancet/javac (merge (javac-defaults project)
			 {:srcdir (or (:java-test-path project)
                                     (:test-path project))
                         :destdir (or (:compile-path project)
				      (str project-root "/classes"))
                         :classpath (lib-path project)}))))
