(ns javascript-externs-generator.ui
  (:require-macros [reagent.ratom :as re])
  (:require [re-frame.core :as rf]
            [goog.dom :as dom]
            [goog.net.jsloader :as jsloader]
            [clojure.string :as string]
            [javascript-externs-generator.extern :refer [extract]]))

(enable-console-print!)

(def default-state {:loaded-urls #{}
                    :externed-namespaces {}
                    :current-namespace nil
                    :current-url nil
                    :current-state nil
                    :loading-js false})

(def default-middleware [rf/trim-v])

(defn get-value [id]
  (.-value (dom/getElement id)))

(defn load-script [url success err]
  (-> (jsloader/load url)
      (.addCallbacks success err)))

(rf/register-handler
  :initialize
  (fn [db _]
    (merge db default-state)))

(rf/register-handler
  :load-script
  (fn [db _]
    (let [url (get-value "js-url")]
      (if (string/blank? url)
        (do
          (js/alert "Please enter the url of your javascript file to be loaded")
          db)
        (do
          (load-script url
                       #(rf/dispatch [:load-succeeded url])
                       #(rf/dispatch [:load-failed url]))
          (assoc db :loading-js true :current-url url))))))

(rf/register-handler
  :generate-extern
  (fn [db _]
    (let [{:keys [externed-namespaces]} db
          namespace (get-value "extern-namespace")]
      (if (string/blank? namespace)
        (do
          (js/alert "Please enter a namespace to generate an extern")
          db)
        (assoc db
               :current-namespace namespace
               :externed-namespaces (assoc externed-namespaces
                                           namespace (extract namespace)))))))

(rf/register-handler
  :load-failed
  [default-middleware]
  (fn [db [url]]
    (let [{:keys [loading-js]} db]
      (if loading-js
        (assoc db :loading-js false :current-state :failed)
        db))))

(rf/register-handler
  :load-succeeded
  [default-middleware]
  (fn [db [url]]
    (let [{:keys [loading-js loaded-urls]} db]
      (if loading-js
        (assoc db
               :loading-js false
               :loaded-urls (conj loaded-urls url)
               :current-state :succeeded)
        db))))

(rf/register-handler
  :show-namespace
  [default-middleware]
  (fn [db [namespace]]
    (assoc db :current-namespace namespace)))


(rf/register-sub
  :displayed-extern
  (fn [db]
    (let [{:keys [externed-namespaces current-namespace]} @db]
      (re/reaction (get externed-namespaces current-namespace)))))

(rf/register-sub
  :loading-js
  (fn [db]
    (re/reaction (get @db :loading-js))))

(rf/register-sub
  :loaded-urls
  (fn [db]
    (re/reaction (get @db :loaded-urls))))

(rf/register-sub
  :externed-namespaces
  (fn [db]
    (re/reaction (get @db :externed-namespaces))))

(defn extern-output []
  (let [extern (rf/subscribe [:displayed-extern])]
    [:textarea {:id "extern-output"
                :rows 25
                :cols 100
                :read-only true
                :value @extern}]))

(defn loading-icon []
  [:img {:src "images/loading.gif"
         :height 20
         :width 20}])

(defn loading-js []
  (let [loading (rf/subscribe [:loading-js])]
    (when @loading
      [loading-icon])))

(defn loaded-javascripts []
  (let [loaded-urls (rf/subscribe [:loaded-urls])]
    (when (seq @loaded-urls)
      [:div {:id "loaded-javascripts"}
       [:p "Loaded Javascripts:"]
       [:ul
        (for [url @loaded-urls]
          [:li {:key url} url])]])))

(defn externed-namespaces[]
  (let [externed-namespaces (rf/subscribe [:externed-namespaces])]
    (when (seq @externed-namespaces)
      [:div {:id "externed-namespaces"}
       [:p "Externed Namespaces:"]
       [:ul
        (for [namespace (keys @externed-namespaces)]
          [:li {:key namespace}
           [:span {:on-click #(rf/dispatch [:show-namespace namespace])}
            namespace]])]
       [extern-output]])))

(defn extern-generator []
  [:div
   [:div {:id "title"}
    [:h1 "Javascript Externs Generator"]]

   [:div {:id "load-js"}
    [:p "Load your javascript file:"]
    [:span "Url: "]
    [:input {:type "text"
             :id "js-url"
             :size 120}]
    [loading-js]
    [:input {:type "button"
             :value "Load"
             :on-click #(rf/dispatch [:load-script])}]]

   [loaded-javascripts]
   [:div {:id "generate-externs"}
    [:p "Enter the javascript object you want to extern i.e. jQuery:"]
    [:input {:type "text"
             :id "extern-namespace"}]
    [:input {:type "button"
             :value "Extern!"
             :on-click #(rf/dispatch [:generate-extern])}]]

   [externed-namespaces]])
