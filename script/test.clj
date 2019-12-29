#!/usr/bin/env bb

(require '[clojure.java.shell :as shell]
         '[spartan.test :as test :refer [deftest is -main]])

(defn run-namespaces []
  (shell/sh "deps.clj" "-A:test" "-Scommand" "bb -cp {{classpath}} -m spartan.test -n spartan.test-test"))

(def expected-run-namespaces
  {:exit 2, :out "", :err "FAIL in spartan.test-test/foo. Expected (= 1 2) but got false.\nERROR in spartan.test-test/bar. Expected (/ 1 0) but got java.lang.ArithmeticException: Divide by zero\nRan 3 tests in total, 1 tests failed, 1 tests gave errors, 1 succeeded.\n"})

(defn run-vars []
  (shell/sh "deps.clj" "-A:test" "-Scommand" "bb -cp {{classpath}} -m spartan.test -v spartan.test-test/baz"))

(def expected-run-vars
  {:exit 0, :err "Ran 1 tests in total, 1 succeeded.\n", :out ""})

(deftest spartan-test
  (is (= expected-run-namespaces (run-namespaces)))
  (is (= expected-run-vars (run-vars))))

(-main)
