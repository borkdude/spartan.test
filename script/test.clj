#!/usr/bin/env bb

(require '[clojure.java.shell :as shell]
         '[clojure.string :as str]
         '[spartan.test :as test :refer [deftest is -main]])

(defn run-namespaces []
  (-> (shell/sh "deps.clj" "-A:test" "-Scommand" "bb -cp {{classpath}} -m spartan.test -n spartan.test-test")
      :err
      (str/trim)))

(def expected-run-namespaces (str/trim "FAIL in spartan.test-test/failure-test
expected: (= 1 2)
  actual: (not (= 1 2))

FAIL in spartan.test-test/multiple-assertions-test
expected: false
  actual: false

FAIL in spartan.test-test/multiple-assertions-test
expected: nil
  actual: nil

WARNING: no assertions were made in test spartan.test-test/no-assertions-test

FAIL in spartan.test-test/with-testing
something's wrong another testing
expected: false
  actual: false

FAIL in spartan.test-test/thrown?-test
expected: (thrown? Exception (/ 1 1))
  actual: 1

Ran 6 tests containing 7 assertions.
5 failures, 0 errors."))

(defn run-vars []
  (:err (shell/sh "deps.clj" "-A:test" "-Scommand" "bb -cp {{classpath}} -m spartan.test -v spartan.error-test/error-test")))

;; show normal output:
#_(println (run-namespaces))
#_(println (run-vars))

(deftest spartan-test
  (is (= expected-run-namespaces (run-namespaces)))
  (is (str/includes? (run-vars) "1 errors")))

(-main)
