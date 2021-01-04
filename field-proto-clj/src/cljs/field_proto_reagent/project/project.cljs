(ns field-proto-reagent.project.project
  (:require
    [field-proto-reagent.project.sidebar :as sidebar]
    [field-proto-reagent.project.map :as map]
    [reagent.session :as session]))



(defn page []
 (fn []
   (let [routing-data (session/get :route)
         item (get-in routing-data [:route-params :pname])]
             [:span.main2
              [:div {:class "row"}
               [:div {:class "col-md-8 p-0"} [map/component]]
               [:div {:class "col-md-4 p-0"} [sidebar/page item]]
              ]
              ])))
