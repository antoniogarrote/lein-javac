;;; javac plugin

(ns leiningen.compile-java
  (:require lancet)
  (:use [leiningen.compile])
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

(defn test-path [testsuite project]
  (apply make-path
	 (:source-path testsuite)
	 (:fixture-path testsuite)
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

(defn compile-directory
  ([project srcdir destdir classpath]
     (lancet/javac (merge (javac-defaults project)
			  {:srcdir srcdir :destdir destdir :classpath classpath})))
  ([project srcdir destdir]
     (compile-directory project srcdir destdir (lib-path project))))

(defn compile-java [project]
  (compile-directory project
		     (or (:java-source-path project)
			 (:source-path project))
		     (or (:java-compile-path project)
			 (:compile-path project)
			 (str (:root project) "/classes"))))

(defn compile-testsuite [project name]
  (let [tests (:java-tests project)
	testsuite (get tests name)]
    (compile-directory project
		       (:source-path testsuite)
		       (:compile-path testsuite)
		       (test-path testsuite project))))
