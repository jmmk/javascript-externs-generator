(ns javascript-externs-generator.ui.handlers
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [goog.net.jsloader :as jsloader]
            [javascript-externs-generator.ui.db :refer [default-state]]
            [javascript-externs-generator.extern :refer [extract]]))

(def default-middleware [rf/trim-v])

(defn load-script [url success err]
  (-> (jsloader/load url)
      (.addCallbacks success err)))

(defn error-string [error]
  (str error.name ": " error.message ". Check console for stack trace."))

(rf/register-handler
  :initialize
  (fn [_ _] default-state))

(rf/register-handler
  :load-script
  (fn [db _]
    (let [url (:url-text db)]
      (if (string/blank? url)
        (do
          (rf/dispatch [:alert "Blank Field Error" "Please enter the url of your JavaScript file to be loaded"])
          db)
        (do
          (rf/dispatch [:ga-event
                        {:category "button"
                         :action "click"
                         :label "Load"}
                        {:dimension "dimension3"
                         :value url}])
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
          (rf/dispatch [:alert "Blank Field Error" "Please enter a namespace to generate an extern"])
          db)
        (do
          (rf/dispatch [:namespace-text-change ""])
          (rf/dispatch [:ga-event
                        {:category "button"
                         :action "click"
                         :label "Extern!"}
                        {:dimension "dimension1"
                         :value namespace-text}])
          (try
            (assoc db
                   :current-namespace namespace-text
                   :externed-namespaces (assoc externed-namespaces
                                               namespace-text (extract namespace-text)))
            (catch js/Error e
              (.error js/console e)
              (rf/dispatch [:ga-event
                            {:category "button"
                             :action "click"
                             :label "Extern!"}
                            {:dimension "dimension2"
                             :value namespace-text}])
              (rf/dispatch [:alert "Error generating extern" (error-string e)])
              db)))))))

(rf/register-handler
  :ga-event
  [default-middleware]
  (fn [db [event custom]]
    (let [ga (aget js/window "ga")
          {:keys [category action label]} event
          {:keys [dimension value]} custom]
      (ga "send" "event" category action label 1 (clj->js {dimension value})))
    db))

(rf/register-handler
  :alert
  [default-middleware]
  (fn [db [heading text]]
    (assoc db :alert {:heading heading :text text})))

(rf/register-handler
  :close-alert
  (fn [db _]
    (assoc db :alert {:heading "" :text ""})))

(rf/register-handler
  :load-failed
  [default-middleware]
  (fn [db [url]]
    (assoc db :loading-js false)))

(rf/register-handler
  :load-succeeded
  [default-middleware]
  (fn [db [url]]
    (rf/dispatch [:url-text-change ""])
    (assoc db
           :loading-js false
           :loaded-urls (conj (:loaded-urls db) url))))

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
