(ns spartan.error-test
  (:require [spartan.test :refer [deftest is]])
  #_(:require [clojure.test :refer [deftest is]]))

(deftest error-test
  (is (= 1 (/ 1 0))))

