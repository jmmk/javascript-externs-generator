(ns javascript-externs-generator.extern
  (:require [clojure.string :as string]))

(defn get-type [obj]
  (if (fn? obj)
    :function
    :object))

(defn parent-type? [obj]
  (or (object? obj)
      (fn? obj)))

(defn build-tree [obj name]
  {:name name
   :type (get-type obj)
   :props (for [k (js-keys obj)
                :let [child (aget obj k)]]
            (if (and (parent-type? child)
                     (not (identical? obj child)))
              (build-tree child k)
              {:name k :type (get-type child)}))
   :prototype (for [k (js-keys (.-prototype obj))]
                {:name k :type :function})})

(defn build-function [name]
  (str (quote-string name) ": function () {}"))

(defn build-object [name]
  (str (quote-string name) ": {}"))

(defn build-props-extern [obj]
  (let [{:keys [name type props]} obj]
    (cond
      (and (empty? props)
           (not= type :function))
      (build-object name)

      (and (empty? props)
           (= type :function))
      (build-function name)

      :else
      (let [props-extern (str "{" (string/join "," (for [p props]
                                                     (build-props-extern p))) "}")]
        (if (get obj :top)
          (str "var " name "=" props-extern ";")
          (str (quote-string name) ":" props-extern))))))

(defn build-prototype [namespace prototype]
  (when (seq prototype)
    (str namespace ".prototype = {" (string/join "," (for [p prototype]
                                                       (build-function (:name p)))) "};")))

(defn build-prototype-extern [obj namespace]
  (let [{:keys [name props prototype]} obj
        prototype-extern (build-prototype namespace prototype)
        child-prototype-externs (for [p props]
                                  (build-prototype-extern p (str namespace "." (:name p))))]
    (str prototype-extern (string/join child-prototype-externs))))

(defn extract [name]
  (let [tree (assoc (build-tree (aget js/window name) name) :top true)
        props-extern (build-props-extern tree)
        prototype-extern (build-prototype-extern tree name)
        output (str props-extern prototype-extern)
        beautify-options (clj->js {:indent_size 2
                                   :indent_char " "
                                   :preserve_newlines true
                                   :space_after_anon_function true
                                   :jslint_happy false})]
    (js/js_beautify output beautify-options)))
