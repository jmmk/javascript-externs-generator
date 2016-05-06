(ns javascript-externs-generator.extern-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [javascript-externs-generator.extern :as extern]))

(enable-console-print!)

(deftest function
  (let [js-object (clj->js {"testFunction" #()})]
    (is (= "var TEST = {\"testFunction\": function () {}};"
           (extern/extract "TEST" js-object)))))

(deftest property
  (let [js-object (clj->js {"testProperty" "value"})]
    (is (= "var TEST = {\"testProperty\": {}};"
           (extern/extract "TEST" js-object)))))

(deftest object
  (let [js-object (clj->js {"testObject" {"testProperty" "value"}})]
    (is (= "var TEST = {\"testObject\": {\"testProperty\": {}}};"
           (extern/extract "TEST" js-object)))))

(deftest prototype
  (let [js-object (clj->js {"testFunction" #()})]
    (set! (.. js-object -testFunction -prototype -testPrototypeFunction) #())
    (is (= "var TEST = {\"testFunction\": function () {}};TEST.testFunction.prototype = {\"testPrototypeFunction\": function () {}};"
           (extern/extract "TEST" js-object)))))
