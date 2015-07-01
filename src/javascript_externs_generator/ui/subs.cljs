(ns javascript-externs-generator.ui.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as rf]))

(rf/register-sub
  :url-text
  (fn [db]
    (reaction (:url-text @db))))

(rf/register-sub
  :namespace-text
  (fn [db]
    (reaction (:namespace-text @db))))

(rf/register-sub
  :current-namespace
  (fn [db]
    (reaction (:current-namespace @db))))

(rf/register-sub
  :displayed-extern
  (fn [db]
    (let [current-namespace (rf/subscribe [:current-namespace])]
      (reaction (get-in @db [:externed-namespaces @current-namespace])))))

(rf/register-sub
  :loading-js
  (fn [db]
    (reaction (:loading-js @db))))

(rf/register-sub
  :loaded-urls
  (fn [db]
    (reaction (:loaded-urls @db))))

(rf/register-sub
  :externed-namespaces
  (fn [db]
    (reaction (:externed-namespaces @db))))

(rf/register-sub
  :alert
  (fn [db]
    (reaction (:alert @db))))
