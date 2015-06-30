(ns javascript-externs-generator.ui
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]
            [re-com.core :as rc]
            [goog.net.jsloader :as jsloader]
            [clojure.string :as string]
            [javascript-externs-generator.extern :refer [extract]]))

(enable-console-print!)

(def default-state {:loaded-urls #{}
                    :externed-namespaces {}
                    :current-namespace nil
                    :url-text ""
                    :namespace-text ""
                    :loading-js false})

(def default-middleware [rf/trim-v])

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
    (let [url (get db :url-text)]
      (if (string/blank? url)
        (do
          (js/alert "Please enter the url of your javascript file to be loaded")
          db)
        (do
          (load-script url
                       #(rf/dispatch [:load-succeeded url])
                       #(rf/dispatch [:load-failed url]))
          (assoc db :loading-js true))))))

(rf/register-handler
  :generate-extern
  (fn [db _]
    (let [{:keys [externed-namespaces namespace-text]} db]
      (if (string/blank? namespace-text)
        (do
          (js/alert "Please enter a namespace to generate an extern")
          db)
        (do
          (rf/dispatch [:namespace-text-change ""])
          (assoc db
                 :current-namespace namespace-text
                 :externed-namespaces (assoc externed-namespaces
                                             namespace-text (extract namespace-text))))))))

(rf/register-handler
  :load-failed
  [default-middleware]
  (fn [db [url]]
    (let [{:keys [loading-js]} db]
      (assoc db :loading-js false))))

(rf/register-handler
  :load-succeeded
  [default-middleware]
  (fn [db [url]]
    (let [{:keys [loading-js loaded-urls]} db]
      (rf/dispatch [:url-text-change ""])
      (assoc db
             :loading-js false
             :loaded-urls (conj loaded-urls url)))))

(rf/register-handler
  :show-namespace
  [default-middleware]
  (fn [db [namespace]]
    (assoc db :current-namespace namespace)))

(rf/register-handler
  :url-text-change
  [default-middleware]
  (fn [db [url-text]]
    (assoc db :url-text url-text)))

(rf/register-handler
  :namespace-text-change
  [default-middleware]
  (fn [db [namespace-text]]
    (assoc db :namespace-text namespace-text)))

(rf/register-sub
  :url-text
  (fn [db]
    (reaction (get @db :url-text))))

(rf/register-sub
  :namespace-text
  (fn [db]
    (reaction (get @db :namespace-text))))

(rf/register-sub
  :current-namespace
  (fn [db]
    (reaction (get @db :current-namespace))))

(rf/register-sub
  :displayed-extern
  (fn [db]
    (let [current-namespace (rf/subscribe [:current-namespace])]
      (reaction (get-in @db [:externed-namespaces @current-namespace])))))

(rf/register-sub
  :loading-js
  (fn [db]
    (reaction (get @db :loading-js))))

(rf/register-sub
  :loaded-urls
  (fn [db]
    (reaction (get @db :loaded-urls))))

(rf/register-sub
  :externed-namespaces
  (fn [db]
    (reaction (get @db :externed-namespaces))))

(defn extern-output []
  (let [extern (rf/subscribe [:displayed-extern])]
    (fn []
      [rc/input-textarea
       :model extern
       :on-change #()
       :width "100%"
       :rows 25])))

(defn loading-js []
  (let [loading (rf/subscribe [:loading-js])]
    (fn []
      (when @loading
        [rc/throbber :style {:margin "6px"}]))))

(defn loaded-javascripts []
  (let [loaded-urls (rf/subscribe [:loaded-urls])]
    (fn []
      (when (seq @loaded-urls)
        [:div
         [rc/title
          :label "Loaded JavaScripts:"
          :level :level3]
         [:ul
          (for [url @loaded-urls]
            [:li {:key url} url])]]))))

(defn externed-namespaces []
  (let [externed-namespaces (rf/subscribe [:externed-namespaces])
        current-namespace (rf/subscribe [:current-namespace])]
    (fn []
      (when (seq @externed-namespaces)
        [:div
         [rc/title
          :label "Externed Namespaces:"
          :level :level3]
         [rc/horizontal-tabs
          :tabs (for [namespace (keys @externed-namespaces)]
                  {:id namespace
                   :label namespace})
          :model current-namespace
          :on-change #(rf/dispatch [:show-namespace %])]
         [extern-output]]))))

(defn load-js []
  (let [url-text (rf/subscribe [:url-text])]
    (fn []
      [:div
       [rc/title
        :label "Load your JavaScript file:"
        :level :level3]
       [rc/h-box
        :gap "5px"
        :children [[rc/input-text
                    :model url-text
                    :on-change #(rf/dispatch [:url-text-change %])
                    :placeholder "http://code.jquery.com/jquery-1.9.1.js"
                    :width "700px"
                    :attr {:id "js-url"}]
                   [loading-js]]]
       [rc/button
        :label "Load"
        :on-click #(rf/dispatch [:load-script])]])))

(defn generate-externs []
  (let [namespace-text (rf/subscribe [:namespace-text])]
    (fn []
      [:div
       [rc/title
        :label "Enter the JavaScript object you want to extern:"
        :level :level3]
       [rc/input-text
        :model namespace-text
        :on-change #(rf/dispatch [:namespace-text-change %])
        :placeholder "jQuery"
        :attr {:id "extern-namespace"}]
       [rc/button
        :label "Extern!"
        :on-click #(rf/dispatch [:generate-extern])]])))

(defn extern-generator []
  [rc/v-box
   :gap "10px"
   :margin "20px"
   :width "50%"
   :children [[rc/title
               :label "JavaScript Externs Generator"
               :underline? true
               :level :level1]

              [load-js]

              [loaded-javascripts]

              [generate-externs]

              [externed-namespaces]]])
