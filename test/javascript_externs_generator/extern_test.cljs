(ns javascript-externs-generator.extern-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [javascript-externs-generator.extern :as extern]))

(enable-console-print!)

(defn compare!
  "eval and extract input, then compare it with the expected output"
  [expected, js-string]
  (js/eval js-string)
  (is (= expected (extern/extract "TEST" js/TEST))))

(deftest top-level-empty-function
  (let [js-string "function TEST() {}"
        expected "var TEST = function(){};"]
    (compare! expected js-string)))

(deftest function-with-property
  "Functions containing properties should be output as an object with properties"
  (let [js-string "function TEST(){}; TEST.testProperty = \"value\""
        expected "var TEST = {\"testProperty\": {}};"]
    (compare! expected js-string)))

(deftest top-level-empty-object
  (let [js-string "var TEST = {}"
        expected "var TEST = {};"]
    (compare! expected js-string)))

(deftest object-with-property
  (let [js-string "var TEST = {testProperty: \"value\"}"
        expected "var TEST = {\"testProperty\": {}};"]
    (compare! expected js-string)))

(deftest object-with-function
  (let [js-string "var TEST = {testFunction: function(){}}"
        expected "var TEST = {\"testFunction\": function(){}};"]
    (compare! expected js-string)))

(deftest nested-object
  (let [js-string "var TEST = {testObject: {testProperty: \"value\"}}"
        expected "var TEST = {\"testObject\": {\"testProperty\": {}}};"]
    (compare! expected js-string)))

(deftest prototype
  (let [js-string "var TEST = {testFunction: function(){}}; TEST.testFunction.prototype.testPrototypeFunction = function(){}"
        expected "var TEST = {\"testFunction\": function(){}};TEST.testFunction.prototype = {\"testPrototypeFunction\": function(){}};"]
    (compare! expected js-string)))

(deftest circular-reference
  (let [js-string "var TEST = {}; TEST.prop = TEST;"
        expected "var TEST = {\"prop\": {\"prop\": {}}};"]
    (compare! expected js-string)))

(deftest nested-circular-reference
  (let [js-string "var TEST = {prop: {}}; TEST.prop.circular = TEST;"
        expected "var TEST = {\"prop\": {\"circular\": {\"prop\": {}}}};"]
    (compare! expected js-string)))

(deftest dom-node
  (let [js-string "var TEST = {canvas: document.createElement('canvas')};"
        expected "var TEST = {\"canvas\": {}};"]
    (compare! expected js-string)))
