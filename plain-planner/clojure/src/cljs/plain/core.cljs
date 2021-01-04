(ns plain.core
    (:require-macros [secretary.core :refer [defroute]])
    (:import goog.history.Html5History)
    (:require [plain.navbar :as navbar]
              [plain.people :as people]
              [plain.timeline :as timeline]
              [plain.goals :as goals]
              [plain.api :as api]
              [plain.state :as state]
              [secretary.core :as secretary]
              [goog.events :as events]
              [reagent.core :as reagent]
              [goog.history.EventType :as EventType]
              [cljs.core.async :refer [<!]]))


(enable-console-print!) ; println, prn, .log js/console


(defn hook-browser-navigation! []
  (doto (Html5History.)
        (events/listen
         EventType/NAVIGATE
         (fn [event]
           (secretary/dispatch! (.-token event))))
        (.setEnabled true)))


(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" [] (swap! state/app-state assoc :page :home))
  (defroute "/people" [] (swap! state/app-state assoc :page :people))
  (defroute "/goals" [] (swap! state/app-state assoc :page :goals))
  (hook-browser-navigation!))


(defmulti current-page #(@state/app-state :page))
(defmethod current-page :home [] [timeline/page])
(defmethod current-page :people [] [people/page])
(defmethod current-page :goals [] [goals/page])
(defmethod current-page :default [][:div ])


(defn ^:export main []
  (app-routes)
  (api/get-projects)
  (api/get-goals)
  (api/get-people)
  (api/get-timeline)
  (reagent/render [current-page]
                  (. js/document (getElementById "app"))))
                  ;(.getElementById js/document "app")))



(defn on-js-reload [] (prn "rerender"))



(main)
