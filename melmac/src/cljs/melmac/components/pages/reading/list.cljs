(ns melmac.components.pages.reading.list
  (:require
    [melmac.components.pages.reading.styles :as styles]
    [melmac.components.pages.list-item :as list-item]))

(defn component
  [atoms api]
  (let [{:keys [get-all-items
                select-item!
                selected-item-id
                item-title
                text-metadata]} (merge atoms api)]
    (fn []
      ; to re-render on change
      @selected-item-id
      @text-metadata
      (let [list-item:component (list-item/component selected-item-id item-title select-item! :page)]
        [:<>
         (doall (map list-item:component (get-all-items)))]))))