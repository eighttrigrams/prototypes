(ns field-proto-reagent.project.map
  (:require
    [field-proto-reagent.docs :as docs]
    [reagent.core :as reagent]))

(defn render []
  [:div#map])


(defn mount []
  (let [
    {:keys [lat long zoom]} (:geometry (:resource (:active @docs/_)))
    map (.setView (.map js/L "map") #js [lat long] zoom)
    ]
    (-> js/L
      (.tileLayer "http://a.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png"
                  (clj->js {:attribution "Map data &copy; [...]"
                            :maxZoom 18}))
      (.addTo map))
  ))

(defn component []
  (reagent/create-class {:reagent-render render
                         :component-did-mount mount}))
