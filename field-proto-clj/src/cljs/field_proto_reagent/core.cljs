(ns field-proto-reagent.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.dom :as dom]
              [reagent.session :as session]
              [reitit.frontend :as reitit]
              [clerk.core :as clerk]
              [field-proto-reagent.util :as util]
              [field-proto-reagent.overview.overview :as overview]
              [field-proto-reagent.project.project :as project]
              [accountant.core :as accountant]))

(util/path-for :about)


(defn about-page []
  (fn [] [:span.main
          [:h1 "About field-proto-reagent"]]))


(defn page-for [route]
  (case route
    :index #'overview/page
    :project #'project/page
    :about #'about-page))


(defn navbar [] (fn []
  [:div#navbar
   [:a#brand {:href (util/path-for :index)} "Field-Proto-Reagent"]
   [:a#info  {:href (util/path-for :about) :class "float-right"} "Info"]]))


(defn top-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div#top-page
       [navbar]
       [page]
       [:footer]])))


(defn mount-root []
  (dom/render [top-page] (.getElementById js/document "app")))


(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path util/router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)
        ))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path util/router path)))})
  (accountant/dispatch-current!)
  (mount-root))
