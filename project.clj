(defproject javascript-externs-generator "0.1.0-SNAPSHOT"
  :description "JavaScript Externs Generator"
  :url "http://jmmk.github.io/javascript-externs-generator"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [re-frame "0.5.0"]
                 [re-com "0.5.4"]
                 [cljsjs/js-beautify "1.6.2-0"]
                 [org.clojure/clojurescript "1.8.51"]]

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-doo "0.1.6"]
            [lein-figwheel "0.5.0-6"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["out"
                                    "out-adv"
                                    "out-node"
                                    "bin"
                                    "assets/js/javascript_externs_generator.js"
                                    "target"]

  :figwheel {:css-dirs ["assets/css"]}
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     {:on-jsload "javascript-externs-generator.ui.core/re-render"}
                        :compiler     {:main           "javascript-externs-generator.ui.core"
                                       :output-to      "assets/js/javascript_externs_generator.js"
                                       :output-dir     "out"
                                       :optimizations  :none
                                       :cache-analysis true
                                       :source-map     true}}
                       {:id           "test"
                        :source-paths ["src" "test"]
                        :compiler     {:output-to     "target/test.js"
                                       :output-dir    "target"
                                       :main          "javascript-externs-generator.runner"
                                       :optimizations :none}}
                       {:id           "release-npm"
                        :source-paths ["src"]
                        :compiler     {:main          "javascript-externs-generator.cli"
                                       :output-to     "bin/extern"
                                       :output-dir    "out-node"
                                       :optimizations :advanced
                                       :target        :nodejs}}
                       {:id           "release-web"
                        :source-paths ["src"]
                        :compiler     {:main          "javascript-externs-generator.ui.core"
                                       :output-to     "assets/js/javascript_externs_generator.js"
                                       :output-dir    "out-adv"
                                       :optimizations :advanced
                                       :pretty-print  false}}]})
