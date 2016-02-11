(ns javascript-externs-generator.extern-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest one-equals-one
  (is (= 1 1)))

(run-tests)
