(ns melmac.components.pages.helper)

(defn jump-to [id]
  (js/setTimeout
   #(.scrollIntoView
     (.getElementById js/document (str "page-list-item-" id))
     (clj->js {:block :nearest :behavior :smooth}))
   100))