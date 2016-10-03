(ns javascript-externs-generator.ui.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [goog.dom :as dom]
            [javascript-externs-generator.ui.subs]
            [javascript-externs-generator.ui.handlers]
            [javascript-externs-generator.ui.views :as ui]))

(defn ^:export re-render []
  (reagent/render [ui/extern-generator] (dom/getElement "extern-generator")))

(defn ^:export init []
  (rf/dispatch-sync [:initialize])
  (re-render))
