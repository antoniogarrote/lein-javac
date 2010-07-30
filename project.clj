(defproject lein-javac "1.2.1-SNAPSHOT"
  :description "Java compiler plugin for Leiningen."
  :dependencies [[ant/ant-launcher "1.6.5"]
                 [org.clojure/clojure "1.2.0-beta1"]
                 [org.clojure/clojure-contrib "1.2.0-beta1"]]
  :dev-dependencies [[leiningen/leiningen "1.2.1-SNAPSHOT"]
                     [robert/hooke "1.0.2"]
                     [swank-clojure "1.2.1"]])
