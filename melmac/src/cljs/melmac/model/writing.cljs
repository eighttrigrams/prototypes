(ns melmac.model.writing
  (:require [melmac.model.helper :as helper]))

(defn update- [text changeset]
  (helper/update- text changeset :sections))

(def new-section
  {:title "no title" :content []})

(defn delete-section [writing id]
  (helper/delete-item writing id :sections :selected-section))

(defn create-citation [text]
  (helper/create-item text new-section :sections))