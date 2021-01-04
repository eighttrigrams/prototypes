(ns field-proto-reagent.project.sidebar
  (:require
    [reagent.core :as reagent]
    [reagent.session :as session]
    [field-proto-reagent.docs :as docs]
    [field-proto-reagent.util :as util]))


(defn sidebar-render [resource-id]
  (let [document (:active @docs/_)]
    [:div.sidebar.project-sidebar
      [:h3 (:identifier (:resource document))
        [:a {:href (util/path-for :index) :class "float-right"}  "X"]]
      [:div.project-sidebar-body
        [:b (:short-description (:resource document))]
        [:br] [:br]
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
        sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
        sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
        Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
        Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
        invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et
        accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est
        Lorem ipsum dolor sit amet."]]))


(defn page [resource-id]
  (reagent/create-class {:reagent-render sidebar-render}))
