(ns field-proto-reagent.overview.map
  (:require
    [reagent.core :as reagent]
    [field-proto-reagent.docs :as docs]
    [field-proto-reagent.overview.search :as search]))


(defonce themap (reagent/atom {:link nil}))

(defonce markers (reagent/atom (.layerGroup js/L)))


(defn add-marker-to-map!
    [lat long markers]
      (.addTo (.marker js/L #js [lat long]) markers))


(defn add-active-marker-to-map!
  [lat long markers]
    (let [icon-struct (js-obj "icon" (.icon js/L
      (js-obj
        "iconUrl" "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-yellow.png"
        "iconAnchor" #js [12 41]
        "iconSize" #js [25 41])))]
      (.addTo (.marker js/L #js [lat long] icon-struct) markers)))


(defn add-markers [markers documents active-document]
  (doseq [document
    (if (= nil active-document)
      documents
      (filter #(not= (-> % :resource :id) (-> active-document :resource :id)) documents))]
    (add-marker-to-map!
      (:lat (:geometry (:resource document)))
      (:long (:geometry (:resource document)))
      markers))
  (when (not= nil active-document)
    (add-active-marker-to-map!
      (:lat (:geometry (:resource active-document)))
      (:long (:geometry (:resource active-document))) markers)))


(defn render []
  (let [{:keys [documents active]} @docs/_
        markers @markers]

    (.clearLayers markers)
    (add-markers markers documents active)

    [:div#map]))


(defn update-link [mapref link]
  (assoc mapref :link link))


(defn mount []
  (let [map (.setView (.map js/L "map") #js [51.505 -0.09] 2)
        markers @markers]
    #_(.log js/console "didmount start")
    (swap! themap update-link map)
    (-> js/L
      (.tileLayer "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"
                  (clj->js {:attribution "Map data &copy; [...]"
                            :maxZoom 18}))
      (.addTo map))
    (.addTo markers map)
    (search/renew-search)
    #_(.log js/console "didmount stop")))


(defn component []
  (reagent/create-class {:reagent-render render
                         :component-did-mount mount}))
