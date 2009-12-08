;;; javac plugin


(ns leiningen.compile-java
  (:require [other-lancet])
  (:use [leiningen.compile])
  (:refer-clojure :exclude [compile]))

(defn lib-path [project]
  (apply make-path
         (:source-path project)
         (:test-path project)
         (:compile-path project)
         (:resources-path project)
         (find-lib-jars project)))

(defn compile-java [project]
  (let [project-root (:source-path project)]
    (other-lancet/javac {:srcdir (str project-root "/src")
                         :destdir (str project-root "/classes")
                         :includejavaruntime "yes"
                         :classpath (lib-path project)
                         :debug (or (str (:javac-debug project))
                                    "false")
                         :target (or (str (:javac-target project))
                                     "1.5")})))