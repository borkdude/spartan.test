#!/usr/bin/env bb

(require '[clojure.java.shell :as shell]
         '[clojure.string :as str]
         '[spartan.test :as test :refer [deftest is -main]])

(def expected-run-namespaces (str/trim "
FAIL in spartan.test-test/failure-test
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

FAIL in spartan.test-test/thrown-with-msg?-test
expected: (thrown-with-msg? Exception #\"zero\" (/ 1 1))
  actual: 1

Ran 7 tests containing 9 assertions.
6 failures, 0 errors."))

(defn run-namespaces []
  (let [{:keys [:err :out]}
        (shell/sh "deps.clj" "-A:test"
                  "-Scommand" "bb -cp {{classpath}} -m spartan.test" "-n" "spartan.test-test")]
    (when-not (str/blank? out)
      (println out))
    (str/trim err)))

(defn run-vars []
  (:err (shell/sh "deps.clj" "-A:test"
                  "-Scommand" "bb -cp {{classpath}} -m spartan.test -v spartan.error-test/error-test")))

(def expected-run-tests
  (str/trim "
FAIL in foo/foo-test
expected: (= 1 2)
  actual: (not (= 1 2))

Ran 1 tests containing 1 assertions.
1 failures, 0 errors."))

(defn run-tests-test []
  (str/trim
   (:err
    (shell/sh "deps.clj" "-A:test" "-Scommand" "bb -cp {{classpath}}" "-e" "(require '[spartan.run-tests-test])"))))

;; show normal output:
#_(println (run-namespaces))
#_(println (run-vars))
#_(prn (run-tests-test))

(deftest spartan-test
  (is (= expected-run-namespaces (run-namespaces)))
  (is (str/includes? (run-vars) "1 errors"))
  (is (= expected-run-tests (run-tests-test))))

(-main "-n" "user")
