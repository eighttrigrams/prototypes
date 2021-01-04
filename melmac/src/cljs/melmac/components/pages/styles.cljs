(ns melmac.components.pages.styles
  (:require [melmac.components.styles :as global-styles]))

(def citations-sidebar-width "16.66%")
(def citations-editor-1-left "16.66%")
(def citations-editor-width "41.66%")
(def citations-editor-2-left "58.32%")

(def sidebar-title
  {:text-overflow :ellipsis
   :white-space   :nowrap
   :width         "90%"
   :position      :absolute
   :top           "0"
   :text-align    :right
   :display       :inline-block
   :overflow      :hidden})

(def sidebar
  {:background-color global-styles/inverted-background-color
   :color            global-styles/widget-background-color
   :width            "100%"
   :position         :relative})

(def sidebar-list-input
  {:background-color global-styles/widget-background-color
   :width            "100%"
   :margin-bottom    "3px"
   :padding-left     "4px"
   :z-index          1000})

(defn sidebar-list-item [selected?]
  {:background-color (if selected? "#b36b00" global-styles/widget-background-color)
   :margin-bottom    "3px"
   :z-index          1000
   :padding-left     "4px"
   :width            "100%"})

(defn citations-editor [position]
  {:height     "100vh"
   :position   :fixed
   :z-index    1000
   :width      citations-editor-width
   :top        0
   :left       (if (= 0 position) citations-editor-1-left citations-editor-2-left)
   :max-height "100vh"})

(defn quill-editor-element []
  {:background-color global-styles/widget-background-color})

(def blend-component
  {:position         :fixed
   :width            "83.33%"
   :height           "100vh"
   :z-index          1001
   :background-color global-styles/inverted-background-color
   :opacity          "0.4"
   :left             "16.66%"
   :top              0})