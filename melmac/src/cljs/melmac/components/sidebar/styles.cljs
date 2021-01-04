(ns melmac.components.sidebar.styles
  (:require [melmac.components.styles :as styles]))

(def texts-sidebar-width "300px")
(def texts-sidebar-total-height "66%")
(def texts-sidebar-list-height "calc(66vh - 24px)")

(def menu-bar-2-height "24px")

; should be a mdi size: 18|24|36|48
(def menu-bars-combined-height "48px")

(def texts-sidebar
  {:z-index          1002
   :width            texts-sidebar-width
   :height           texts-sidebar-total-height
   :max-height       texts-sidebar-total-height
   :background-color styles/widget-background-color
   :position         :fixed
   :left             0
   :top              0})

(def texts-sidebar-contents
  {:overflow-y :scroll
   :position   :fixed
   :top        styles/menu-bar-1-height
   :width      texts-sidebar-width
   :height     texts-sidebar-list-height
   :max-height texts-sidebar-list-height})

(def text-item
  {:background-color styles/inverted-background-color
   :color            styles/widget-background-color
   :margin-bottom    "2px"
   :margin-left      "2px"
   :margin-right     "2px"})

(def blend
  {:position         :fixed
   :width            "100vw"
   :height           "100vh"
   :z-index          1001
   :background-color styles/inverted-background-color
   :opacity          "0.4"
   :left             "0"
   :top              "0"})

