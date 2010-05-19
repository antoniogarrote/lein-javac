(ns leiningen.compile-java-integrations
  (:use leiningen.compile-java))

(defn compile-java-integrations [project & params]
  (compile-testsuite project :integration))
