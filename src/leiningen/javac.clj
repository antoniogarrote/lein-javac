(ns leiningen.javac
  "Compile java source files with Leiningen.

Usage:

  lein javac      - Compile all java sources.
  lein javac PATH - Compile only java sources in PATH.
"
  (:import org.apache.tools.ant.types.Path java.io.File)
  (:use [clojure.contrib.def :only (defvar)]
        [clojure.contrib.string :only (split join)]
        [leiningen.classpath :only (get-classpath make-path)])
  (:require lancet)
  (:refer-clojure :exclude [compile]))

(defvar *java-options*
  {:debug "false" :fork "true" :includejavaruntime "yes" :target "1.5"}
  "The default options for the java compiler.")

(defmethod lancet/coerce [Path String] [_ str]
           (Path. lancet/ant-project str))

(defn expand-path
  "Expand a path fragment relative to the project root. If path starts
  with File/separator it is treated as an absolute path and will not
  be modified."
  [project path]
  (if-not (= (str (first path)) File/separator)
    (str (:root project) File/separator path)
    path))

(defn classpath [project]
  (let [compile-path (expand-path project (:compile-path project))]
    (join File/pathSeparator (map str (conj (get-classpath project) compile-path)))))

(defn- java-options
  "Returns the java compiler options of the project."
  [project] (merge *java-options* (:java-options project)))

(defn extract-javac-task
  "Extract a compile task from the given spec."
  [project [path & options]]
  (let [javac-task (-> (java-options project)
                       (merge {:destdir (:compile-path project) :srcdir path})
                       (merge (apply hash-map options)))]
    (assoc javac-task
      :classpath (classpath project)
      :srcdir (expand-path project (:srcdir javac-task))
      :destdir (expand-path project (:destdir javac-task)))))

(defn extract-javac-tasks
  "Extract all compile tasks of the project."
  [project]
  (let [specs (:java-source-path project)]
    (map #(extract-javac-task project %)
         (if (isa? (class specs) String) [[specs]] specs))))

(defn- run-javac-task
  "Compile the given task spec."
  [task-spec]
  (lancet/mkdir {:dir (:destdir task-spec)})
  (lancet/javac task-spec))

(defn javac [project & [directory]]  
  (doseq [task (extract-javac-tasks project)
          :when (or (nil? directory) (= (expand-path project directory) (:srcdir task)))]    
    (run-javac-task task)))

