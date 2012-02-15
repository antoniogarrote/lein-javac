(ns leiningen.test.javac
  (:import java.io.File)
  (:require leiningen.hooks.javac)
  (:use [leiningen.core :only (defproject read-project)]
        [leiningen.classpath :only (get-classpath)]
        [leiningen.clean :only (clean)]
        clojure.test
        leiningen.javac
        leiningen.test.helper))

(refer-private 'leiningen.javac)

(def project (read-project "sample/project.clj"))

(defn- java-src-task []
  (extract-javac-task project (first (:javac-source-path project))))

(defn- java-test-task []
  (extract-javac-task project (second (:javac-source-path project))))

(defn- main-class-file []
  (File. (str (expand-path project (:destdir (java-src-task)))
              File/separator "Main.class")))

(defn- test-class-file []
  (File. (str (expand-path project (:destdir (java-test-task)))
              File/separator "MainTest.class")))

(defn- compile-directory []
  (File. (expand-path project (:compile-path project))))

(defn- cleanup-classes []
  (map #(.delete %) [(main-class-file) (test-class-file)]))

(deftest test-expand-path
  (is (= (expand-path project "/tmp")
         "/tmp"))
  (is (= (expand-path project "src")
         (str (:root project) File/separator "src"))))

(deftest test-extract-javac-task
  (are [specs expected]
    (is (= (extract-javac-task project specs) (merge (java-options project) expected)))
    ["src/java"]
    {:classpath (classpath project)
     :destdir (expand-path project (:compile-path project))
     :srcdir (expand-path project "src/java")}
    ["src/java" :debug "true"]
    {:classpath (classpath project)
     :destdir (expand-path project (:compile-path project))
     :debug "true"
     :srcdir (expand-path project "src/java")}))

(deftest test-extract-javac-tasks
  (is (= (extract-javac-tasks project) [(java-src-task) (java-test-task)])))

(deftest test-java-options
  (is (= (java-options {}) *java-options*))
  (is (= (java-options project) (assoc *java-options* :debug "true"))))

(deftest test-run-javac-task
  (let [task-spec (java-src-task)]
    (try
      (cleanup-classes)
      (run-javac-task task-spec)
      (is (.exists (File. (:destdir task-spec))))
      (is (.exists (main-class-file)))
      (finally (cleanup-classes)))))

(deftest test-javac
  (try
    (cleanup-classes)
    (javac project)
    (is (.exists (compile-directory)))
    (is (.exists (main-class-file)))
    (is (.exists (test-class-file)))
    (finally (cleanup-classes))))

(deftest test-clean-hook
  (try
    (javac project)
    (is (.exists (main-class-file)))
    (is (.exists (test-class-file)))
    (clean project)
    (is (not (.exists (main-class-file))))
    (is (not (.exists (test-class-file))))
    (finally (cleanup-classes))))
