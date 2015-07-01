(ns javascript-externs-generator.ui.views
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [re-com.core :as rc]))

(defn alert-box []
  (let [alert (rf/subscribe [:alert])]
    (fn []
      (when (not-every? string/blank? [(:heading @alert) (:text @alert)])
        [rc/alert-box
         :id "alert-box"
         :alert-type :danger
         :heading (:heading @alert)
         :body (:text @alert)
         :closeable? true
         :on-close #(rf/dispatch [:close-alert])]))))

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

              [alert-box]

              [load-js]

              [loaded-javascripts]

              [generate-externs]

              [externed-namespaces]]])
