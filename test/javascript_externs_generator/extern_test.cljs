(ns javascript-externs-generator.extern-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [javascript-externs-generator.extern :as extern]))

(enable-console-print!)

(defn compare! [expected, js-string]
  (js/eval js-string)
  (is (= expected (extern/extract "TEST" js/TEST))))

(deftest function
  (let [js-string "function TEST() {}"
        expected "var TEST = {\"testFunction\": function () {}};"]
    (compare! expected js-string)))

(deftest property
  (let [js-string "var TEST = {testProperty: \"value\"}"
        expected "var TEST = {\"testProperty\": {}};"]
    (compare! expected js-string)))

(deftest object
  (let [js-string "var TEST = {testObject: {testProperty: \"value\"}}"
        expected "var TEST = {\"testObject\": {\"testProperty\": {}}};"]
    (compare! expected js-string)))

(deftest prototype
  (let [js-string "var TEST = {testFunction: function(){}}; TEST.testFunction.prototype = {testPrototypeFunction: function() {}}"
        expected "var TEST = {\"testFunction\": function () {}};TEST.testFunction.prototype = {\"testPrototypeFunction\": function () {}};"]
    (compare! expected js-string)))
