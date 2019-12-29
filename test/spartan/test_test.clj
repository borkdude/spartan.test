(ns spartan.test-test
  (:require [spartan.test :refer [deftest is]])
  #_(:require [clojure.test :refer [deftest is]]))

(deftest failure-test
  (is (= 1 2)))

(deftest successful-test
  (is (= 1 1)))

(deftest multiple-assertions-test
  (is false)
  (is nil))

(deftest no-assertions-test)
