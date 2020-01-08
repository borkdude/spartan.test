(ns spartan.test-test
  (:require [spartan.test :refer [deftest is testing]])
  #_(:require [clojure.test :refer [deftest is]]))

(deftest failure-test
  (is (= 1 2)))

(deftest successful-test
  (is (= 1 1)))

(deftest multiple-assertions-test
  (is false)
  (is nil))

(deftest no-assertions-test)

(deftest with-testing
  (testing "something's wrong"
    (testing "another testing"
      (is false))))

(deftest thrown?-test
  (is (thrown? Exception (/ 1 0)))
  (is (thrown? Exception (/ 1 1))))

(deftest thrown-with-msg?-test
  (is (thrown-with-msg? Exception #"zero" (/ 1 0)))
  (is (thrown-with-msg? Exception #"zero" (/ 1 1))))
