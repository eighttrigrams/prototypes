(ns melmac.components.pages.writing.sidebar
  (:require
    [melmac.components.styles :as global-styles]
    [melmac.components.pages.writing.styles :as styles]
    [melmac.components.pages.writing.list :as list]
    [melmac.components.pages.title :as title]
    [melmac.components.pages.helper :as helper]))

(defn- control:component [{add! :add-item!}]
  [:div
   [:span
    {:on-click #(helper/jump-to (add!))}
    [:i {:class (global-styles/mdi "plus-box")}]]])

(defn component [atoms api]
  (fn []
    [:div
     {:style styles/sidebar}
     [title/component (:show-sidebar! api) (:text-metadata atoms)]
     [control:component api]
     [:div
      {:style styles/sidebar-list}
      [list/component atoms api]]]))