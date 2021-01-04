(ns melmac.model.reading
  (:require [melmac.model.helper :as helper]))

(defn update- [text changeset]
  (helper/update- text changeset :citations))

(defn- assert-citation-is-valid!
  [[id citation]]
  (or
   (not (contains? citation :page))
   (not (contains? citation :comment))
   (not (vector? (:comment citation)))
   (not (contains? citation :citation))
   (not (vector? (:citation citation)))))

(defn is-valid?
  "returns true if text is valid, false otherwise"
  [text]
  (let [result
        (or
         (not (contains? text :author))
         (not (contains? text :title))
         (not (contains? text :citations))
         (not (contains? text :selected-citation))
         (not
          (or (= nil (:selected-citation text))
              (contains? (:citations text) (:selected-citation text))))
         (not (every? false? (map assert-citation-is-valid! (:citations text)))))]
    (if (= result true)
      (do
        (prn (dissoc text :citations))
        (prn (:citations text))
        false)
      true)))

(def new-citation
  {:page 0 :citation [] :comment []})

(defn delete-citation [reading id]
  (helper/delete-item reading id :citations :selected-citation))

(defn create-citation [text]
  (helper/create-item text new-citation :citations))