(ns leiningen.test.javac
  (:import java.io.File)
  (:use [leiningen.core :only (defproject read-project)]
        [leiningen.classpath :only (get-classpath make-path)]
        clojure.test
        leiningen.javac
        leiningen.test.helper))

(refer-private 'leiningen.javac)

(def *project* (read-project "sample/project.clj"))

(defn classpath->str
  "Converts the classpath in a compile task to a string. This is
  neccesary because in Java land a Path is not equal to the same Path."
  [compile-task]
  (assoc compile-task :classpath (str (:classpath compile-task))))

(deftest test-expand-path  
  (is (= (expand-path *project* "/tmp")
         "/tmp"))
  (is (= (expand-path *project* "src")
         (str (:root *project*) File/separator "src"))))

(deftest test-extract-compile-task  
  (are [specs expected]
    (is (= (classpath->str (extract-compile-task *project* specs)) (merge (java-options *project*) expected)))
    ["src/java"]
    {:classpath (str (apply make-path (conj (get-classpath *project*) (expand-path *project* "src/java"))))
     :destdir (:compile-path *project*)
     :srcdir (expand-path *project* "src/java")}
    ["src/java" :debug "true"]
    {:classpath (str (apply make-path (conj (get-classpath *project*) (expand-path *project* "src/java"))))
     :destdir (:compile-path *project*)
     :debug "true"
     :srcdir (expand-path *project* "src/java")}))

(deftest test-extract-compile-tasks
  (is (= (map classpath->str (extract-compile-tasks *project*))
         [(classpath->str (extract-compile-task *project* ["src/java"]))
          (classpath->str (extract-compile-task *project* ["test/java" :debug "true"]))])))

(deftest test-java-options
  (is (= (java-options {}) *java-options*))
  (is (= (java-options *project*)
         {:includejavaruntime "yes" :debug "true" :target "1.5" :fork "true"})))

(deftest test-run-compile-task
  (let [task-spec (extract-compile-task *project* ["src/java"])
        main-class (File. (str (:destdir task-spec) File/separator "Main.class"))
        cleanup #(.delete main-class)]
    (try
      (cleanup)
      (run-compile-task task-spec)
      (is (.exists (File. (:destdir task-spec))))
      (is (.exists main-class))
      (finally (cleanup)))))

(deftest test-javac
  (let [target-directory (:compile-path *project*)
        main-class (File. (str target-directory File/separator "Main.class"))
        test-class (File. (str target-directory File/separator "MainTest.class"))
        cleanup (fn [] (map #(.delete %) [main-class test-class]))]
    (try
      (cleanup)
      (javac *project*)
      (is (.exists (File. target-directory)))
      (is (.exists main-class))
      (is (.exists test-class))
      (finally (cleanup)))))
