(ns field-proto-reagent.overview.sidebar
  (:require
    [field-proto-reagent.util :as util]
    [field-proto-reagent.overview.search :as search]
    [field-proto-reagent.docs :as docs]
    [reagent.core :as rg]
    [ajax.core :refer [GET]]))


(defn visit-document [project-document]
  (docs/set-active! project-document))


(defn convert-to-li [project-document]
  [:a
   {:href (util/path-for :project {:pname (:id (:resource project-document))})
    :key (str "a-" (:id (:resource project-document)))}
   [:div.document
    {:key (:id (:resource project-document))
     :on-mouse-enter #(visit-document project-document)}
    [:div (:identifier (:resource project-document))]
    [:div.short-description (:short-description (:resource project-document))]]])


(defn search-component [search-string]
  [:span.search {:class "float-right"}
    [:input {
      :type "text"
      :value search-string
      :placeholder ""
      :on-change #(search/renew-search (aget % "target" "value"))}]
    [:i {:class "mdi mdi-magnify"}]])


(defn sidebar-render []
  (let [search @search/q
        documents (:documents @docs/_)]
    [:div.sidebar.overview-sidebar
    [:h3 "Projekte " [search-component search]]
    [:div (map convert-to-li documents)]]))


(defn page []
  (rg/create-class {:reagent-render sidebar-render}))
