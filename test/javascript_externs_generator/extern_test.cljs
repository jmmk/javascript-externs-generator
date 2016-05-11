(ns javascript-externs-generator.extern-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [javascript-externs-generator.extern :as extern]))

(enable-console-print!)

(defn compare!
  "eval and extract input, then compare it with the expected output"
  [expected, js-string]
  (js/eval js-string)
  (is (= expected (extern/extract "TEST" js/TEST))))

(deftest top-level-function
  (let [js-string "function TEST() {}"
        expected "var TEST = {\"testFunction\": function () {}};"]
    (compare! expected js-string)))

(deftest object-with-property
  (let [js-string "var TEST = {testProperty: \"value\"}"
        expected "var TEST = {\"testProperty\": {}};"]
    (compare! expected js-string)))

(deftest empty-object
  (let [js-string "var TEST = {}"
        expected "var TEST = {};"]
    (compare! expected js-string)))

(deftest nested-object
  (let [js-string "var TEST = {testObject: {testProperty: \"value\"}}"
        expected "var TEST = {\"testObject\": {\"testProperty\": {}}};"]
    (compare! expected js-string)))

(deftest prototype
  (let [js-string "var TEST = {testFunction: function(){}}; TEST.testFunction.prototype.testPrototypeFunction = function(){}"
        expected "var TEST = {\"testFunction\": function () {}};TEST.testFunction.prototype = {\"testPrototypeFunction\": function () {}};"]
    (compare! expected js-string)))
