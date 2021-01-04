(ns melmac.components.sidebar.component
  (:require [reagent.core :as rg]
            [melmac.context.readings :as readings]
            [melmac.context.writings :as writings]
            [melmac.components.styles :as global-styles]
            [melmac.components.sidebar.styles :as styles]))

(defn text:component [select-text! selected-menu-item]
  (fn [text]
    [:div
     {:key      text
      :class    ["pl-1"]
      :style    styles/text-item
      :on-click #(select-text! text (if (= @selected-menu-item 0) "reading" "writing"))}
     text]))

(defn blend []
  [:div
   {:style styles/blend}])


(defn component:inner [show-sidebar select-text! selected-menu-item]
  (fn []
    (let [available-texts (if (= 0 @selected-menu-item) (readings/list-) (writings/list-))]
      [:div
       (blend)
       [:div
        {:id    :texts-sidebar
         :class "pl-0 pr-0 ml-0 mr-0"
         :style styles/texts-sidebar}
        [:div
         {:id     :texts-sidebar-menu-bar-1
          :height global-styles/menu-bar-1-height}
         [:i
          {:class    (global-styles/mdi "menu")
           :on-click #(reset! show-sidebar false)}]
         [:span
          {:class    [:float-right "pr-1"]
           :style    {:font-weight (if (= @selected-menu-item 1) :bold 13)}
           :on-click #(reset! selected-menu-item 1)}
          "Writings"]
         [:span
          {:class    [:float-right "pr-1"]
           :style    {:font-weight (if (= @selected-menu-item 0) :bold 13)}
           :on-click #(reset! selected-menu-item 0)}
          "Readings"]]
        [:div
         {:id    :texts-sidebar-contents
          :style styles/texts-sidebar-contents}
         (doall (map (text:component select-text! selected-menu-item) available-texts))]]])))

(defn component
  "
  "
  [show-sidebar select-text! text-type]
  (let [selected-menu-item (rg/atom (if (= text-type "reading") 0 1))]
    (fn []
      [component:inner show-sidebar select-text! selected-menu-item])))

