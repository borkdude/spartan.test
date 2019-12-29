(ns spartan.impl.test
  {:no-doc true}
  (:require [clojure.string :as str]))

(def init-counter {:fail 0 :error 0 :success 0})
(def report-counter (atom init-counter))
(def ^:dynamic *current-test* nil)
(def registered-tests (atom #{}))

(defn print-summary [{:keys [:error :fail :success]}]
  (println (str
            (str/join ", "
                      (filter some?
                              [(format "Ran %s tests in total" (+ error fail success))
                               (when (pos? fail)
                                 (format "%s tests failed" fail))
                               (when (pos? error)
                                 (format "%s tests gave errors" error))
                               (format "%s succeeded" success)]))
            ".")))

(defn run-tests
  ([] (run-tests nil))
  ([tests]
   (reset! report-counter init-counter)
   (if tests
     (doseq [t tests]
       (when-let [t (resolve t)]
         (t)))
     (doseq [t @registered-tests]
       (when-let [t (resolve t)]
         (t))))
   (let [res @report-counter]
     (print-summary res)
     res)))

(defn parse-args [args]
  (loop [ret {}
         args args]
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
