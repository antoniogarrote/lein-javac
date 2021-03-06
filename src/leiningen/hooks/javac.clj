(ns leiningen.hooks.javac
  (:require leiningen.clean leiningen.compile)
  (:use [clojure.java.io :only [file delete-file]]
        [leiningen.javac :only (javac extract-javac-tasks)]
        robert.hooke))

(defn clean-javac-hook [task & args]
  (apply task args)
  (doseq [task (extract-javac-tasks (first args))]
    (if-let [directory (:destdir task)]
      (doseq [file (file-seq (java.io.File. directory))]
        (delete-file file true)))))

(defn compile-javac-hook [task & args]
  (apply javac args)
  (apply task args))

(add-hook #'leiningen.clean/clean clean-javac-hook)
(add-hook #'leiningen.compile/compile compile-javac-hook)
