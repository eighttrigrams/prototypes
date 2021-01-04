(ns field-proto-reagent.util
  (:require [reitit.frontend :as reitit]))

(def router
  (reitit/router
   [["/" :index]
   ["/project"
        ["/:pname" :project]]
    ["/about" :about]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))
