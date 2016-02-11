(ns javascript-externs-generator.handlers-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [javascript-externs-generator.ui.handlers :as handlers]))

(deftest parse-error
  (try (throw (js/Error. "ERROR"))
       (catch js/Error e
         (is (= "Error: ERROR. Check console for stack trace."
                (handlers/error-string e))))))
