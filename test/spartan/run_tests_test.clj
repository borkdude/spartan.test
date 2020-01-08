(ns spartan.run-tests-test)

(ns foo
  (:require [spartan.test :refer [deftest is]]))

(deftest foo-test
  (is (= 1 2)))

(ns bar
  (:require [spartan.test :refer [deftest is]]))

(deftest bar-test
  (is (= 1 2)))

(ns baz
  (:require [spartan.test :as t]))

(t/run-tests 'foo)
