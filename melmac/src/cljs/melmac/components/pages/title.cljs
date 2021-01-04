(ns melmac.components.pages.title
  (:require
    [melmac.components.styles :as global-styles]
    [melmac.components.pages.styles :as styles]))

(defn component [show-sidebar! om:text-metadata]
  (let [title (:title @om:text-metadata)]
    [:div
     {:style styles/sidebar}
     [:div
      {:id    :citations-sidebar-menu-bar-1
       :style {:height global-styles/menu-bar-1-height}}
      [:i
       {:class    (global-styles/mdi "menu")
        :on-click show-sidebar!}]
      [:span
       {:class "float-right pr-1"
        :style styles/sidebar-title}
       title]]]))