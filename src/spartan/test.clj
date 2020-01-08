(ns spartan.test
  "A light test framework compatible with babashka."
  (:require [spartan.impl.test :as impl]))

(defmacro is
  ([form] `(is ~form nil))
  ([form msg]
   `(impl/try-expr ~msg ~form)))

(defmacro deftest [symbol & body]
  `(let [sym# (symbol (str (ns-name *ns*))
                      (str '~symbol))]
     (defn ~symbol []
       (binding [impl/*current-test* sym#]
         ~@body))
     (do (swap! impl/registered-tests conj sym#)
         (var ~symbol))))

(defmacro testing [string & body]
  `(binding [impl/*testing-contexts* (conj impl/*testing-contexts* ~string)]
     ~@body))

(defn run-tests
  "Runs all tests in the given namespaces; prints results.
  Defaults to current namespace if none given.  Returns a map
  summarizing test results."
  ([] (run-tests *ns*))
  ([& namespaces]
   (let [summary (assoc (apply merge-with + (map impl/test-ns namespaces))
                        :type :summary)]
     #_(impl/do-report summary)
     summary)))

(defn- exit [summary]
  (let [{:keys [:error :fail]} summary ]
    (System/exit (+ error fail))))

(defn -main [& args]
  (let [{:keys [:namespaces :tests] :as _parsed} (impl/parse-args args)]
    (cond (seq namespaces)
          (exit (apply run-tests namespaces))
          (seq tests)
          (do
            (doseq [v tests]
              (require (symbol (namespace v))))
            (exit (impl/run-tests tests))))))

;;;; Scratch
