(ns leiningen.hooks.javac
  (:require leiningen.clean leiningen.compile)
  (:use [clojure.contrib.io :only [file delete-file delete-file-recursively]]
        [leiningen.javac :only (javac extract-javac-tasks)]
        robert.hooke))

(defn clean-javac-hook [task & args]
  (apply task args)
  (let [tasks (extract-javac-tasks (first args))]
    (delete-file-recursively (:destdir task))))

(defn compile-javac-hook [task & args]
  (apply javac args)
  (apply task args))

(add-hook #'leiningen.clean/clean clean-javac-hook)
(add-hook #'leiningen.compile/compile compile-javac-hook)
