(ns spartan.test-test
  (:require [spartan.test :refer [deftest is]]))

(deftest foo
  (is (= 1 2)))

(deftest bar
  (is (/ 1 0)))

(deftest baz
  (is (= 1 1)))
