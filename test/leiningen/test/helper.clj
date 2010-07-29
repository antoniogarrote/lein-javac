(ns leiningen.test.helper)

(defn refer-private [ns]
  (doseq [[symbol var] (ns-interns ns)]
    (when (:private (meta var))
      (intern *ns* symbol var))))

