(ns spartan.run-tests-test)

(ns foo
  (:require [spartan.test :as t :refer [deftest is]]))

(deftest foo-test
  (is (= 1 2)))

(t/run-tests)

(ns bar
  (:require [spartan.test :as t :refer [deftest is]]))

(deftest bar-test
  (is (= 1 2)))

(t/run-tests)

(ns baz
  (:require [spartan.test :as t]))

(t/run-tests 'foo)
