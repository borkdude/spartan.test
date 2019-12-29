(ns spartan.impl.test
  {:no-doc true}
  (:require [clojure.string :as str]))

(def init-counter {:fail #{} :error #{} :success #{}})
(def report-counter (atom init-counter))
(def ^:dynamic *current-test* nil)
(def registered-tests (atom #{}))

(defn print-summary [{:keys [:error :fail :success]}]
  (binding [*out* *err*]
    (println (str
              (str/join ", "
                        (filter some?
                                [(format "Ran %s tests in total" (+ error fail success))
                                 (when (pos? fail)
                                   (format "%s tests failed" fail))
                                 (when (pos? error)
                                   (format "%s tests gave errors" error))
                                 (format "%s succeeded" success)]))
              "."))))

(defn run-tests
  ([] (run-tests nil))
  ([tests]
   (reset! report-counter init-counter)
   (if tests
     (doseq [t tests]
       (when-let [v (resolve t)]
         (v)))
     (doseq [t @registered-tests]
       (when-let [v (resolve t)]
         (v))))
   (let [{:keys [:error :fail :success]} @report-counter
         res {:error (count error)
              :fail (count fail)
              :success (count success)}]
     (print-summary res)
     res)))

(defn parse-args [args]
  (loop [ret {}
         args (seq args)]
    (if args
      (case (first args)
        "-n" (recur (update ret :namespaces (fnil conj #{})
                            (symbol (second args)))
                    (nnext args))
        "-v" (recur (update ret :tests (fnil conj #{})
                            (symbol (second args)))
                    (nnext args))
        (throw (Exception. (format "Unrecognized option: %s" (first args)))))
      ret)))

;;;; Scratch
