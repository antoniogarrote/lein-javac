(ns leiningen.javac
  "Compile java sources with Leiningen.

Usage:

  lein javac      - Compile all java source directories specified in project.clj.
  lein javac PATH - Compile only the java source directories specified in project.clj.

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
        (merge (apply hash-map options))
        (merge {:classpath (apply make-path (conj (get-classpath project) srcdir))
                :destdir (:compile-path project)
                :srcdir srcdir}))))

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

;; (defn compile-directory
;;   "Compile all Java files in the source directory and write the class
;;   files to the target directory."
;;   [source-directory target-directory & java-options]
;;   (let [java-options (apply hash-map java-options)]
;;     (lancet/mkdir {:dir target-directory})
;;     (lancet/javac (merge java-options {:srcdir source-directory :destdir target-directory}))))

;; (defn lib-path [project]
;;   (apply make-path
;;          (:source-path project)
;;          (:test-path project)
;;          (:compile-path project)
;;          (:resources-path project)
;;          (find-lib-jars project)))

;; (defn test-path [project testsuite]
;;   (apply make-path
;;          (:source-path testsuite)
;;          (:fixture-path testsuite)
;;          (:compile-path project)
;;          (:resources-path project)
;;          (:compile-path project)
;;          (find-lib-jars project)))

;; (defn lib-path [project]
;;   (apply make-path
;;          (:source-path project)
;;          (:test-path project)
;;          (:compile-path project)
;;          (:resources-path project)
;;          (find-lib-jars project)))

;; ;; (defn compile-java [project]
;; ;;   (let [project-root (:root project)]
;; ;;     (lancet/javac {:srcdir (or (:java-source-path project)
;; ;;                                (:source-path project))
;; ;;                    :destdir (str project-root "/classes")
;; ;;                    :includejavaruntime "yes"
;; ;;                    :classpath (lib-path project)
;; ;;                    :debug (or (:javac-debug project)
;; ;;                               "false")
;; ;;                    :target (or (:javac-target project)
;; ;;                                "1.5")
;; ;;                    :fork (or (:javac-fork project)
;; ;;                              "true")})))

;; ;; (defn javac [project & [task-name]]  
;; ;;   (doseq [compile-task (extract-compile-jobs project)]
;; ;;     (if (or (nil? task-name) (= task-name (:name compile-task)))
;; ;;       (apply compile-directory
;; ;;              (:srcdir compile-task)
;; ;;              (:compile-path project)
;; ;;              ;; (flatten (seq (java-options project (:java-options compile-task))))
;; ;;              ))))


;; ;; (defn javac [project & [task-name]]  
;; ;;   (for [compile-task (extract-compile-jobs project)]
;; ;;     (if (or (nil? task-name) (= task-name (:name compile-task)))      
;; ;;       (flatten (seq (assoc (merge (java-options project) (:java-options compile-task))
;; ;;                       :classpath (conj (get-classpath project) (:srcdir compile-task))))))))
