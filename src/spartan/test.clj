(ns spartan.test
  "A light test framework compatible with babashka."
  (:require [clojure.string :as str]))

(def ^:private init-counter {:fail 0 :error 0 :success 0})
(def report-counter (atom init-counter))
(def ^:dynamic *current-test* nil)

(defmacro is [& body]
  `(try
     (let [res# (do ~@body)]
       (if res#
         (swap! report-counter update :success inc)
         (do (binding [*out* *err*]
               (println (format "FAIL in %s. Expected %s but got %s." *current-test* '~@body res#))
               (swap! report-counter update :fail inc)))))
     (catch java.lang.Exception e#
       (binding [*out* *err*]
         (println (format "ERROR in %s. Expected %s but got %s" *current-test* '~body e#))
         (swap! report-counter update :error inc)))))
(def ^:private registered-tests (atom #{}))

(defmacro deftest [symbol & body]
  `(let [sym# (symbol (str (ns-name *ns*))
                      (str '~symbol))]
     (defn ~symbol [] (binding [*current-test* sym#]
                        ~@body))
       (swap! registered-tests conj sym#)))

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

(defn- parse-args [args]
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

(defn -main [& args]
  (let [{:keys [:namespaces :tests] :as _parsed} (parse-args args)]
    (doseq [n namespaces]
      (require n))
    (doseq [v tests]
      (require (symbol (namespace v))))
    (let [{:keys [:error :fail]} (run-tests tests)]
      (System/exit (+ error fail)))
    (println "Expected: initial namespace to load.")))

;;;; Scratch
