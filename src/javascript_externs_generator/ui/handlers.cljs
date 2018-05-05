(ns javascript-externs-generator.ui.handlers
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [goog.net.jsloader :as jsloader]
            [goog.dom :as dom]
            [goog.object :as obj]
            [goog.html.legacyconversions :as conv]
            [cljsjs.js-beautify]
            [javascript-externs-generator.ui.db :refer [default-state]]
            [javascript-externs-generator.extern :refer [extract-loaded]]))

(def default-middleware [rf/trim-v])

(defn load-script [url success err]
  (let [converted-url (conv/trustedResourceUrlFromString url)
        sandbox (dom/getElement "sandbox")
        options #js {:document (obj/get sandbox "contentDocument")}]
    (-> (jsloader/safeLoad converted-url options)
        (.addCallbacks success err))))

(defn error-string [error]
  (let [check-console "Check console for stack trace."]
    (cond
      (nil? error)
      (str "Unknown Error: " check-console)

      (string? error)
      error

      :else
      (str (.-name error) ": " (.-message error) ". " check-console))))

(def beautify-options (clj->js {:indent_size               2
                                :indent_char               " "
                                :preserve_newlines         true
                                :space_after_anon_function true
                                :jslint_happy              false}))
(defn beautify [output]
  (js/js_beautify output beautify-options))

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
          (try
            (assoc db
              :current-namespace namespace-text
              :externed-namespaces (assoc externed-namespaces
                                     namespace-text (beautify (extract-loaded namespace-text))))
            (catch :default e
              (.error js/console e)
              (rf/dispatch [:alert "Error generating extern" (error-string e)])
              db)))))))

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
