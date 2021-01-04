(ns melmac.components.pages.reading.styles
  (:require
    [melmac.components.styles :as global-styles]
    [melmac.components.pages.styles :as pages-styles]))

(def citations-list-height "calc(100vh - 48px)")

(def sidebar
  {:width    pages-styles/citations-sidebar-width
   :position :fixed
   :top      0
   :left     0})

(def sidebar-list
  {:overflow-y :scroll
   :height     citations-list-height
   :width      "100%"})

(defn page:render []
  {:background-color global-styles/main-background-color})

