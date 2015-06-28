(defproject javascript-externs-generator "0.1.0-SNAPSHOT"
  :description "JavaScript Externs Generator"
  :url "http://jmmk.github.io/javascript-externs-generator"

  :dependencies [[org.clojure/clojure "1.7.0-RC2"]
                 [re-frame "0.4.1"]
                 [re-com "0.5.4"]
                 [org.clojure/clojurescript "0.0-3308"]]

  :node-dependencies [[source-map-support "0.2.8"]]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.5"]
            [lein-npm "0.4.0"]]

  :source-paths ["src"]

  :clean-targets ["assets/js/javascript_externs_generator.js"]

  :figwheel {:css-dirs ["assets/css"]}
  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :figwheel {:on-jsload "javascript-externs-generator.core/init"}
              :compiler {
                :main "javascript-externs-generator.core"
                :output-to "assets/js/javascript_externs_generator.js"
                :output-dir "out"
                :optimizations :none
                :cache-analysis true
                :source-map true}}
             {:id "release"
              :source-paths ["src"]
              :compiler {
                :output-to "assets/js/javascript_externs_generator.js"
                :output-dir "out-adv"
                :optimizations :advanced
                :pretty-print false}}]})
