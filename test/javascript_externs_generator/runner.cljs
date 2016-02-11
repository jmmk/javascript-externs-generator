(ns javascript-externs-generator.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [javascript-externs-generator.extern-test]
            [javascript-externs-generator.handlers-test]))

(doo-tests 'javascript-externs-generator.extern-test
           'javascript-externs-generator.handlers-test)
