(defproject javascript-externs-generator "0.1.0-SNAPSHOT"
  :description "JavaScript Externs Generator"
  :url "http://jmmk.github.io/javascript-externs-generator"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [re-frame "0.10.5"]
                 [re-com "2.1.0"]
                 [cljsjs/js-beautify "1.6.8-0"]
                 [org.clojure/clojurescript "1.10.238"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.10"]
            [lein-figwheel "0.5.15"]]

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
                       {:id           "cli"
                        :source-paths ["src"]
                        :compiler     {:main          "javascript-externs-generator.cli"
                                       :output-to     "bin/extern"
                                       :output-dir    "out-cli"
                                       :optimizations :none
                                       :target        :nodejs}}
                       {:id           "release-npm"
                        :source-paths ["src"]
                        :compiler     {:main          "javascript-externs-generator.cli"
                                       :output-to     "bin/extern"
                                       :output-dir    "out-node"
                                       :optimizations :simple
                                       :target        :nodejs}}
                       {:id           "release-web"
                        :source-paths ["src"]
                        :compiler     {:main          "javascript-externs-generator.ui.core"
                                       :output-to     "assets/js/javascript_externs_generator.js"
                                       :output-dir    "out-adv"
                                       :optimizations :advanced
                                       :pretty-print  false}}]})
