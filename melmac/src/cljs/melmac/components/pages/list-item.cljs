(ns melmac.components.pages.list-item
  (:require [melmac.components.pages.styles :as styles]))

(defn- atom-input [value select!]
  [:input
   {:type      "text"
    :value     @value
    :style     styles/sidebar-list-input
    :on-change #(reset! value (-> % .-target .-value))
    :on-click  select!}])

(defn- list-item:component
  [select! om:title page selected?]
  [:<>
   (if selected?
     [atom-input om:title select!]
     [:div
      {:on-click select!
       :style    (styles/sidebar-list-item selected?)}
      page])])

(defn component
  [selected-id:atom om:title select! title-keyword]
  (fn [item]
    (let [selected? (= @selected-id:atom (:id item))
          select!   #(select! (:id item))]
      [:div
       {:key (str "page-list-item-" (:id item))
        :id  (str "page-list-item-" (:id item))}
       [list-item:component select! om:title (get item title-keyword) selected?]])))
