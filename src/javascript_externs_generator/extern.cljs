(ns javascript-externs-generator.extern
  (:require [goog.dom :as dom]
            [clojure.string :as string]))

(defn get-type
  "Differentiate between function property and object property"
  [obj]
  (if (fn? obj)
    :function
    :object))

(defn parent-type?
  "Whether it is possible for this object to have child properties"
  [obj]
  (or (object? obj)
      (fn? obj)))

(defn build-tree
  "Build a recursive tree representation of the JavaScript object
  and metadata about its properties, prototype, and functions"
  [obj name]
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

(defn build-function
  "Return string representation of a function"
  [name]
  (str (quote-string name) ": function () {}"))

(defn build-object
  "Return string representation of an object property"
  [name]
  (str (quote-string name) ": {}"))

(defn build-props-extern
  "Return recursive string representation of an object's properties"
  [obj]
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

(defn build-prototype
  "Return string representation of an object's prototype/functions"
  [namespace prototype]
  (when (seq prototype)
    (str namespace ".prototype = {" (string/join "," (for [p prototype]
                                                       (build-function (:name p)))) "};")))

(defn build-prototype-extern
  "Return recursive string representation of an object's prototype chain"
  [obj namespace]
  (let [{:keys [name props prototype]} obj
        prototype-extern (build-prototype namespace prototype)
        child-prototype-externs (for [p props]
                                  (build-prototype-extern p (str namespace "." (:name p))))]
    (str prototype-extern (string/join child-prototype-externs))))

(defn extract
  "Given the name of a JavaScript object that has been loaded into the global namespace,
  recursively extract properties and prototypes into an extern for use with ClojureScript"
  [name]
  (let [sandbox (dom/getElement "sandbox")
        js-object (-> sandbox (aget "contentWindow") (aget name))
        tree (assoc (build-tree js-object name) :top true)
        props-extern (build-props-extern tree)
        prototype-extern (build-prototype-extern tree name)]
    (str props-extern prototype-extern)))

