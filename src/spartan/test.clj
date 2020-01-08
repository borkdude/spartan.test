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

(defn -main [& args]
  (let [{:keys [:namespaces :tests] :as _parsed} (impl/parse-args args)]
    (doseq [n namespaces]
      (require n))
    (doseq [v tests]
      (require (symbol (namespace v))))
    (let [{:keys [:error :fail]} (impl/run-tests tests)]
      (System/exit (+ error fail)))))

;;;; Scratch
