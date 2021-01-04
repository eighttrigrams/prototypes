(ns field-proto-reagent.overview.overview
  (:require
    [field-proto-reagent.overview.map :as map]
    [field-proto-reagent.overview.sidebar :as sidebar]))


(defn page []
  (fn []
    [:span.main
     [:div {:class "row"}
      [:div {:class "col-md-8 p-0"} [map/component]]
      [:div {:class "col-md-4 p-0"} [sidebar/page]]]]))
