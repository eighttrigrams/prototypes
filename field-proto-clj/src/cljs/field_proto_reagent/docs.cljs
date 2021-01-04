(ns field-proto-reagent.docs
  (:require[reagent.core :as rg]))


(defonce _ (rg/atom {:active nil :documents []}))


(defn set-active! [document]
  (swap! _ (fn [_] (assoc _ :active document))))


(defn determine-active [active documents]
  (if (= active nil)
     nil
     (if (> (count (filter #(= (-> % :resource :id) (-> active :resource :id)) documents)) 0)
       active
       nil)))


(defn update-documents! [documents]
  (swap! _ (fn [_] (assoc _ :documents documents
    :active (determine-active (:active _) documents)))))
