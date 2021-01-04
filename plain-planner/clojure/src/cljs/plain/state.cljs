(ns plain.state
  (:require [reagent.core :as reagent :refer [atom]]))

(def app-state (atom {}))

(defonce timeline (atom []))

(defonce current-project (atom "test3"))

(defonce goals (atom []))

(defonce people (atom []))

(defonce projects (atom []))