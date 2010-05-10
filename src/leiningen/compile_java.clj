;;; javac plugin

(ns leiningen.compile-java
  (:require lancet)
  (:use leiningen.compile)
  (:refer-clojure :exclude [compile]))

(defmethod lancet/coerce [org.apache.tools.ant.types.Path String] [_ str]
  (new org.apache.tools.ant.types.Path lancet/ant-project str))

(defn lib-path [project]
  (apply make-path
         (:source-path project)
         (:test-path project)
         (:compile-path project)
         (:resources-path project)
         (find-lib-jars project)))

(defn javac-defaults [project]
  {:includejavaruntime "yes"
   :debug (or (:javac-debug project)
	      "false")
   :target (or (:javac-target project)
	       "1.5")
   :fork (or (:javac-fork project)
	     "true")})

(defn compile-java [project]
  (let [project-root (:root project)]
    (lancet/javac (merge (javac-defaults project)
			 {:srcdir (or (:java-source-path project)
                                     (:source-path project))
                         :destdir (or (:compile-path project)
				      (str project-root "/classes"))
                         :classpath (lib-path project)}))))
