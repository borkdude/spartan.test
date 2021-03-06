(ns spartan.impl.test
  {:no-doc true})

(def init-counter {:fail 0 :error 0 :success 0 :tests 0 :assertions 0})
(def report-counter (atom init-counter))
(def ^:dynamic *current-test* nil)
(def ^:dynamic *testing-contexts* (list))
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

;; 307
(defn testing-contexts-str
  "Returns a string representation of the current test context. Joins
  strings in *testing-contexts* with spaces."
  []
  (apply str (interpose " " (reverse *testing-contexts*))))


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
           (when (seq *testing-contexts*) (println (testing-contexts-str)))
           (println "expected:" '~form)
           (println "  actual:" (list '~'not (cons '~pred values#)))
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
         (when (seq *testing-contexts*) (println (testing-contexts-str)))
         (println "expected:" '~form)
         (println "  actual:" result#)
         (println)
         (reg-result :fail)))
     result#))

(defn assert-thrown? [msg [_thrown? ex form :as complete-form]]
  `(let [result# (try ~form (catch ~ex e# e#))]
     (if (instance? (resolve '~ex) result#)
       (reg-result :success)
       (binding [*out* *err*]
         (println "FAIL in" *current-test*)
         (when (seq *testing-contexts*) (println (testing-contexts-str)))
         (println "expected:" '~complete-form)
         (println "  actual:" result#)
         (println)
         (reg-result :fail)))
     result#))

(defn assert-thrown-with-msg? [msg [_thrown-with-msg? ex re form :as complete-form]]
  `(let [result# (try ~form (catch ~ex e# e#))]
     (if (and (instance? (resolve '~ex) result#)
              (re-find ~re (ex-message result#)))
       (reg-result :success)
       (binding [*out* *err*]
         (println "FAIL in" *current-test*)
         (when (seq *testing-contexts*) (println (testing-contexts-str)))
         (println "expected:" '~complete-form)
         (println "  actual:" result#)
         (println)
         (reg-result :fail)))
     result#))

(defn assert-expr [msg form]
  (if (sequential? form)
    (let [f (first form)]
      (cond (function? f)
            (assert-predicate msg form)
            (= 'thrown? f)
            (assert-thrown? msg form)
            (= 'thrown-with-msg? f)
            (assert-thrown-with-msg? msg form)
            :else
            (assert-any msg form)))
    (assert-any msg form)))

(defmacro try-expr [msg form]
  `(try ~(assert-expr msg form)
        (catch Exception t# ;; TODO: should be Throwable, but not in bb yet
          (binding [*out* *err*]
            (println "ERROR in" *current-test*)
            (when (seq *testing-contexts*) (println (testing-contexts-str)))
            (println "expected:" '~form)
            (println "  actual:" t#)
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

(defn test-ns
  [ns]
  (let [nsn (if (symbol? ns) ns
                (ns-name ns))
        nsn-str (str nsn)]
    (require nsn)
    (let [tests (filter #(= nsn-str (namespace %)) @registered-tests)]
      (run-tests tests))))

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
