(ns javascript-externs-generator.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [goog.dom :as dom]
            [javascript-externs-generator.ui.subs]
            [javascript-externs-generator.ui.handlers]
            [javascript-externs-generator.ui.views :as ui]))

(defn ^:export init []
  (rf/dispatch-sync [:initialize])
  (reagent/render [ui/extern-generator] (dom/getElement "extern-generator")))
