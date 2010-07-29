(ns leiningen.javac
  "Compile java source files with Leiningen.

Usage:

  lein javac      - Compile all java sources.
  lein javac PATH - Compile only java sources in PATH.
"
  (:import org.apache.tools.ant.types.Path java.io.File)
  (:use [clojure.contrib.def :only (defvar)]
        [leiningen compile]
        [leiningen classpath])
  (:require lancet)
  (:refer-clojure :exclude [compile]))

(defvar *java-options*
  {:debug "false" :fork "true" :includejavaruntime "yes" :target "1.5"}
  "The default options for the java compiler.")

(defmethod lancet/coerce [Path String] [_ str]
           (Path. lancet/ant-project str))

(defn- expand-path [project path]
  (if-not (= (first path) \/)
    (str (:root project) File/separator path)
    path))

(defn- java-options
  "Returns the java compiler options of the project."
  [project] (merge *java-options* (:java-options project)))

(defn- extract-compile-task
  "Extract a compile task from the given spec."
  [project [path & options]]
  (let [srcdir (expand-path project path)]
    (-> (java-options project)
        (merge {:classpath (apply make-path (conj (get-classpath project) srcdir))
                :destdir (:compile-path project)
                :srcdir srcdir})
        (merge (apply hash-map options)))))

(defn- extract-compile-tasks
  "Extract all compile tasks of the project."
  [project]
  (let [specs (:java-source-path project)]
    (map #(extract-compile-task project %)
         (if (isa? (class specs) String) [[specs]] specs))))

(defn run-compile-task
  "Compile the given task spec."
  [task-spec]  
  (lancet/mkdir {:dir (:destdir task-spec)})
  (lancet/javac task-spec))

(defn javac [project & [directory]]  
  (doseq [task (extract-compile-tasks project)
          :when (or (nil? directory) (= (expand-path project directory) (:srcdir task)))]    
    (run-compile-task task)))
