;;; javac plugin


(ns leiningen.compile-java
  "Compile the java source files included in the project"
  (:use [leiningen compile]
        [leiningen classpath])
  (:require [other-lancet])
  (:refer-clojure :exclude [compile]))

(defn lib-path [project]
  (apply make-path
         (:source-path project)
         (:test-path project)
         (:compile-path project)
         (:resources-path project)
         (find-lib-jars project)))

(defn compile-java [project]
  (let [project-root (:root project)]
    (other-lancet/javac {:srcdir (or (:java-source-path project)
                                     (:source-path project))
                         :destdir (str project-root "/classes")
                         :includejavaruntime "yes"
                         :classpath (lib-path project)
                         :debug (or (:javac-debug project)
                                    "false")
                         :target (or (:javac-target project)
                                     "1.5")
                         :fork (or (:javac-fork project)
                                   "true")})))
