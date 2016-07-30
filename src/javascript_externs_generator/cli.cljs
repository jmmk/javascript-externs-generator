(ns javascript-externs-generator.cli
  (:require [cljs.nodejs :as nodejs]
            [javascript-externs-generator.extern :as extern]
            [clojure.string :as string]
            [goog.object :as obj]))

(nodejs/enable-util-print!)

(def fs (nodejs/require "fs"))
(def jsdom (nodejs/require "jsdom"))
(def js-beautify (nodejs/require "js-beautify"))

(def beautify-options #js {:indent_size               2
                           :indent_char               " "
                           :preserve_newlines         true
                           :space_after_anon_function true
                           :jslint_happy              false})

(defn load-js-file [window filename]
  (let [js-code ((obj/get fs "readFileSync") filename)
        script-el (. (obj/get window "document") createElement "script")]
    (obj/set script-el "innerHTML" js-code)
    (obj/set script-el "type" "text/javascript")
    (obj/set script-el "charset" "UTF-8")
    (. (obj/getValueByKeys window "document" "head") appendChild script-el)))

(defn file-args [files]
  (string/split files #","))

(defn -main []
  (let [args (-> (nodejs/require "commander")
                 (.option "-f, --files <files>", "Comma-separated list of JS files to load", file-args)
                 (.option "-n, --namespace <namespace>", "Namespace to extern")
                 (.option "-o, --outfile <outfile>", "Output File for extern")
                 (.parse (obj/get nodejs/process "argv")))
        files (obj/get args "files")
        namespace (obj/get args "namespace")
        outfile (obj/get args "outfile")
        dom-obj ((obj/get jsdom "jsdom"))
        window (obj/get dom-obj "defaultView")]

    ; Validate Required Args
    (when (nil? files)
      (.error js/console "Please specify one or more JavaScript files to load (e.g. -f jquery.js)")
      ((obj/get nodejs/process "exit") 1))
    (when (nil? namespace)
      (.error js/console "Please specify namespace to extern (e.g. -n jQuery)")
      ((obj/get nodejs/process "exit") 1))
    (when (nil? outfile)
      (.error js/console "Please specify an output file for the extern (e.g. -o jquery-extern.js)")
      ((obj/get nodejs/process "exit") 1))

    ; Load JS files
    (doseq [file-name files]
      (load-js-file window file-name))

    ;; Generate Extern
    (let [js-object (obj/get window namespace)]
      (when (nil? js-object)
        (throw (str "Namespace '" namespace "' was not found. Make sure the library is loaded and the name is spelled correctly.")))
      (let [extern-string (extern/wrap-extern namespace (extern/extract namespace js-object))
            beautified ((obj/get js-beautify "js_beautify") extern-string beautify-options)]
        ; Write output
        ((obj/get fs "writeFileSync") outfile beautified)))))

(set! *main-cli-fn* -main)
