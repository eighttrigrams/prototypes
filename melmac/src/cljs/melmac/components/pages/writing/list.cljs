(ns melmac.components.pages.writing.list
  (:require
    [melmac.components.pages.writing.styles :as styles]
    [melmac.components.pages.list-item :as list-item]))

(defn component
  [atoms api]
  (let [{:keys [text-metadata
                select-item!
                selected-item-id
                item-title
                get-all-items]} (merge atoms api)]
    (fn []
      ; to re-render on change
      @selected-item-id
      @text-metadata ;;
      (let [list-item:component (list-item/component selected-item-id item-title select-item! :title)]
        [:<>
         (doall (map list-item:component (get-all-items)))]))))