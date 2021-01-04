(ns field-proto-reagent.styles
  (:require
    [garden.def :refer [defstyles]]))

(defstyles style
  [:body {:background "#ddd"}]

  [:div#map {:height "100%"}
    [:div.leaflet-top {:margin-left "7px"}]]

  [:div#navbar {:background-color "#5572A1" :height "26px" }
    [:a#brand {:color "white" :padding-left "4px"}]
    [:a#info {:color "white" :padding-right "4px"}]]

  [:div.sidebar
    {:height "460px" :background-color "#F5F5F5"}
    [:span.search [:i {:padding-right "20px" :background-color "white" :color "#A9A9A9" :font-size "20px"}]]
    [:input {:padding-top "3px" :border "0px" :padding-left "6px" :color "#A9A9A9" :font-size "18px"}]
    ["input::placeholder" {:color "lightgray"}]
    ["input:focus" {:outline "none"}]
    [:h3 {:padding-left "10px" :padding-top "3px"}]
    ["div.document:hover" {:background-color "#F5F5F5"}]
    ["a:hover" {:text-decoration "none"}]]

  [:div.project-sidebar
    [:h3 [:a {:padding-right "18px" :color "#A9A9A9"}]]
    [:div.project-sidebar-body
      {:background-color "white"
       :height "400px"
       :padding-left "10px"
       :padding-top "3px"
       :padding-right "38px"}]]


  [:div.overview-sidebar
    [:div.document
      {:background-color "white" :padding-left "10px" :padding-top "2px"
       :height "60px" :margin "1px" :color "black"}
      [:div.short-description {:color "gray"}]]]


  [:h1 {:color "#f00"}]
  [:p {:font "18px \"Century Gothic\", Futura, sans-serif"}]

  [:.my-class {:font-size "20px" :background "#ddf"}])
