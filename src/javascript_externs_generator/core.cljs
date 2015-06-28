(ns javascript-externs-generator.core
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [dispatch-sync]]
            [goog.dom :as dom]
            [javascript-externs-generator.ui :as ui]))

(defn ^:export init []
  (dispatch-sync [:initialize])
  (reagent/render [ui/extern-generator] (dom/getElement "extern-generator")))
