(ns javascript-externs-generator.core
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [dispatch-sync]]
            [javascript-externs-generator.ui :as ui]))

(defn init! []
  (dispatch-sync [:initialize])
  (reagent/render [ui/extern-generator] (js/document.getElementById "extern-generator")))

(init!)
