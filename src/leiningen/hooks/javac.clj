(ns leiningen.hooks.javac
  (:require leiningen.compile
            leiningen.jar)
  (:use [leiningen.javac :only (javac)]
        robert.hooke))

(defn compile-java-hook [task & args]
  (apply javac args)
  (apply task args))

(add-hook #'leiningen.compile/compile compile-java-hook)
