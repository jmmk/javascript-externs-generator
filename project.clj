(defproject javascript-externs-generator "0.1.0-SNAPSHOT"
  :description "JavaScript Externs Generator"
  :url "http://jmmk.github.io/javascript-externs-generator"

  :dependencies [[org.clojure/clojure "1.7.0-RC2"]
                 [cljsjs/jquery "1.9.1-0"]
                 [cljsjs/react "0.13.3-0"]
                 [re-frame "0.4.1"]
                 [org.clojure/clojurescript "0.0-3308"]]

  :node-dependencies [[source-map-support "0.2.8"]]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.5"]
            [lein-npm "0.4.0"]]

  :source-paths ["src"]

  :clean-targets ["out" "out-adv"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :figwheel true
              :compiler {
                :main javascript-externs-generator.core
                :output-to "out/javascript_externs_generator.js"
                :output-dir "out"
                :optimizations :none
                :cache-analysis true
                :source-map true}}
             {:id "release"
              :source-paths ["src"]
              :compiler {
                :main javascript-externs-generator.core
                :output-to "out-adv/javascript_externs_generator.min.js"
                :output-dir "out-adv"
                :optimizations :advanced
                :pretty-print false}}]})
