(ns javascript-externs-generator.ui
  (:require [re-frame.core :refer [subscribe dispatch register-handler trim-v]]
            [cljsjs.jquery]
            [clojure.string :as string]
            [javascript-externs-generator.extern :refer [extract]]))

(def default-state {:loaded-urls []
                    :externed-namespaces {}
                    :current-namespace nil
                    :current-url nil
                    :current-state nil
                    :loading false})

(def default-middleware [trim-v])

(register-handler
  :initialize
  (fn [db _]
    (merge db default-state)))

(register-handler
  :load-script
  (fn [db _]
    (let [url (.val (js/jQuery "#js-url"))]
      (if (string/blank? url)
        (do
          (js/alert "Please enter the url of your javascript file to be loaded")
          db)
        (do
          (.setTimeout js/window #(do (dispatch [:load-failed url])) 5000)
          (.getScript js/jQuery url #(do (dispatch [:load-succeeded url])))
          (assoc db :loading true :current-url url))))))

(register-handler
  :generate-extern
  (fn [db _]
    (let [namespace (.val (js/jQuery "#extern-namespace"))]
      (if (string/blank? namespace)
        (do
          (js/alert "Please enter a namespace to generate an extern")
          db)
        (do
          (.html (js/jQuery "#result") (extract namespace))
          db)))))

(register-handler
  :load-failed
  [default-middleware]
  (fn [db [url]]
    (let [{:keys [loading]} db]
      (if loading
        (assoc db :loading false :current-state :failed)
        db))))

(register-handler
  :load-succeeded
  [default-middleware]
  (fn [db [url]]
    (let [{:keys [loading loaded-urls]} db]
      (if loading
        (assoc db :loading false :loaded-urls (conj loaded-urls url) :current-state :succeeded)
        db))))

(defn extern-generator []
  [:div
   [:h1 "Javascript Externs Generator"]
   [:p "Load your javascript file:"
    [:br]
    "Url:"
    [:input {:type "text"
             :id "js-url"
             :size 120}]
    [:br]
    [:input {:type "button"
             :value "Load"
             :on-click #(dispatch [:load-script])}]]
   [:p "Enter the javascript object you want to extern i.e. jQuery:"
    [:br]
    [:input {:type "text"
             :id "extern-namespace"}]]
   [:br]
   [:input {:type "button"
            :value "Extern!"
            :on-click #(dispatch [:generate-extern])}]
   [:br]
   [:textarea {:id "result"
               :rows 25
               :cols 100}]])
