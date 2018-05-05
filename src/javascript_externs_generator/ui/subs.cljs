(ns javascript-externs-generator.ui.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]))

(rf/reg-sub-raw
  :url-text
  (fn [db]
    (reaction (:url-text @db))))

(rf/reg-sub-raw
  :namespace-text
  (fn [db]
    (reaction (:namespace-text @db))))

(rf/reg-sub-raw
  :current-namespace
  (fn [db]
    (reaction (:current-namespace @db))))

(rf/reg-sub-raw
  :displayed-extern
  (fn [db]
    (let [current-namespace (rf/subscribe [:current-namespace])]
      (reaction (get-in @db [:externed-namespaces @current-namespace])))))

(rf/reg-sub-raw
  :loading-js
  (fn [db]
    (reaction (:loading-js @db))))

(rf/reg-sub-raw
  :loaded-urls
  (fn [db]
    (reaction (:loaded-urls @db))))

(rf/reg-sub-raw
  :externed-namespaces
  (fn [db]
    (reaction (:externed-namespaces @db))))

(rf/reg-sub-raw
  :alert
  (fn [db]
    (reaction (:alert @db))))
