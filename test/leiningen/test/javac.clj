(ns leiningen.test.javac
  (:import java.io.File)
  (:use [leiningen.core :only (defproject read-project)]
        [leiningen.classpath :only (get-classpath make-path)]
        [leiningen.clean :only (clean)]
        [clojure.contrib.def :only (defvar)]
        clojure.test
        leiningen.javac
        leiningen.test.helper))

(refer-private 'leiningen.javac)

(defvar *project* (read-project "sample/project.clj")
  "The sample project file.")

(defvar *java-src-spec* (first (:java-source-path *project*))
  "The specification of the java sources.")

(defvar *java-test-spec* (second (:java-source-path *project*))
  "The specification of the java test sources.")

(defn classpath->str
  "Converts the classpath in a compile task to a string. This is
  neccesary because in Java land a Path is not equal to the same Path."
  [javac-task]
  (assoc javac-task :classpath (str (:classpath javac-task))))

(deftest test-expand-path  
  (is (= (expand-path *project* "/tmp")
         "/tmp"))
  (is (= (expand-path *project* "src")
         (str (:root *project*) File/separator "src"))))

(deftest test-extract-javac-task  
  (are [specs expected]
    (is (= (classpath->str (extract-javac-task *project* specs)) (merge (java-options *project*) expected)))
    ["src/java"]
    {:classpath (str (apply make-path (conj (get-classpath *project*) (expand-path *project* "src/java"))))
     :destdir (:compile-path *project*)
     :srcdir (expand-path *project* "src/java")}
    ["src/java" :debug "true"]
    {:classpath (str (apply make-path (conj (get-classpath *project*) (expand-path *project* "src/java"))))
     :destdir (:compile-path *project*)
     :debug "true"
     :srcdir (expand-path *project* "src/java")}))

(deftest test-extract-javac-tasks
  (is (= (map classpath->str (extract-javac-tasks *project*))
         [(classpath->str (extract-javac-task *project* ["src/java"]))
          (classpath->str (extract-javac-task *project* ["test/java" :destdir "build/unit/classes"]))])))

(deftest test-java-options
  (is (= (java-options {}) *java-options*))
  (is (= (java-options *project*)
         {:includejavaruntime "yes" :debug "true" :target "1.5" :fork "true"})))

(deftest test-run-javac-task
  (let [task-spec (extract-javac-task *project* ["src/java"])
        main-class (File. (str (:destdir task-spec) File/separator "Main.class"))
        cleanup #(.delete main-class)]
    (try
      (cleanup)
      (run-javac-task task-spec)
      (is (.exists (File. (:destdir task-spec))))
      (is (.exists main-class))
      (finally (cleanup)))))

(deftest test-javac
  (let [target-directory (:compile-path *project*)
        main-class (File. (str target-directory File/separator "Main.class"))
        test-class (File. (str "build/unit/classes" File/separator "MainTest.class"))
        cleanup (fn [] (map #(.delete %) [main-class test-class]))]
    (try
      (cleanup)
      (javac *project*)
      (is (.exists (File. target-directory)))
      (is (.exists main-class))
      (is (.exists test-class))
      (finally (cleanup)))))
