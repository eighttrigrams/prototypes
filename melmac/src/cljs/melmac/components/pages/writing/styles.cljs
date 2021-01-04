(ns melmac.components.pages.writing.styles
  (:require [melmac.components.styles :as styles]
            [melmac.components.pages.styles :as widgets]))

(def sidebar-width "16.66%")

(def sidebar-list-height "calc(100vh - 48px)")

(def page:render
  {:background-color styles/main-background-color})

(def sidebar
  {:width    sidebar-width
   :position :fixed
   :top      0
   :left     0})

(def sidebar-list
  {:overflow-y :scroll
   :height     sidebar-list-height
   :width      "100%"})

(def blend
  {:position :fixed
   :top 0
   :left widgets/citations-editor-2-left
   :height "100vh"
   :width widgets/citations-editor-width
   :background-color :black})