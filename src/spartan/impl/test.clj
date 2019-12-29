(ns spartan.impl.test
  {:no-doc true}
  (:require [clojure.string :as str]))

(def init-counter {:fail 0 :error 0 :success 0 :tests 0 :assertions 0})
(def report-counter (atom init-counter))
(def ^:dynamic *current-test* nil)
(defn reg-test []
  (swap! report-counter update :tests inc))
(defn reg-result [k]
  (let [res (swap! report-counter
                  (fn [counter]
                    (-> counter
                        (update k inc)
                        (update :assertions inc))))]
    res))
(def registered-tests (atom []))

(defn function?
  [x]
  (if (symbol? x)
    (when-let [v (resolve x)]
      (when (or (fn? v) (and (var? v) (fn? @v)))
        (if-let [m (meta v)]
          (not (some m [:sci/macro :macro]))
          true)))
    (fn? x)))

(defn assert-predicate
  [msg form]
  (let [args (rest form)
        pred (first form)]
    `(let [values# (list ~@args)
           result# (apply ~pred values#)]
       (if result#
         (reg-result :success)
         (binding [*out* *err*]
           (println "FAIL in" *current-test*)
           (println "expected:" '~form)
           (println "actual:" (list '~'not (cons '~pred values#)))
           (println)
           (reg-result :fail)))
       result#)))

(defn assert-any
  [msg form]
  `(let [result# ~form]
     (if result#
       (reg-result :success)
       (binding [*out* *err*]
         (println "FAIL in" *current-test*)
         (println "expected:" '~form)
         (println "actual:" result#)
         (println)
         (reg-result :fail)))
     result#))

(defn assert-expr [msg form]
  (cond (and (sequential? form) (function? (first form)))
        (assert-predicate msg form)
        :else
        (assert-any msg form)))

(defmacro try-expr [msg form]
  `(try ~(assert-expr msg form)
     (catch Exception t# ;; TODO: should be Throwable, but not in bb yet
       (binding [*out* *err*]
         (println "ERROR in" *current-test*)
         (println "expected:" '~form)
         (println "actual:" t#)
         (println)
         (reg-result :error)
         t#))))

(defn print-summary [{:keys [:error :fail :assertions :tests]}]
  (binding [*out* *err*]
    (println "Ran" tests "tests containing" assertions "assertions.")
    (println fail "failures," error "errors.")))

(defn assert-assertions [report-before test-name]
  (let [{assertions-before :assertions} report-before
        {:keys [:assertions]} @report-counter]
    (when (= assertions-before assertions)
      (binding [*out* *err*]
        (println "WARNING: no assertions were made in test" test-name)
        (println)))))

(defn run-tests
  ([] (run-tests nil))
  ([tests]
   (reset! report-counter init-counter)
   (if tests
     (doseq [t tests]
       (when-let [v (resolve t)]
         (reg-test)
         (let [report-before @report-counter]
           (v)
           (assert-assertions report-before t))))
     (doseq [t @registered-tests]
       (when-let [v (resolve t)]
         (reg-test)
         (let [report-before @report-counter]
           (v)
           (assert-assertions report-before t)))))
   (let [res @report-counter]
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
