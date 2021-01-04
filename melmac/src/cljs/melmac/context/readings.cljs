(ns melmac.context.readings
  (:require
    [melmac.model.reading :as reading]
    [melmac.config :as config]
    [melmac.datastore.filesystem :as datastore]))

(def reading-id (atom nil))
(def reading (atom {}))

(defn- get-path
  ([] (str config/store-path "readings/"))
  ([text-id] (str config/store-path "readings/" text-id)))

(defn- update-safely! [updated-text]
  (if (not (reading/is-valid? updated-text))
    (prn "Something went wrong")
    (datastore/update-safely! updated-text
                              (get-path @reading-id)
                              #(reset! reading updated-text))))

(defn initialize [reading-id-]
  (.log js/console "Initialize project" reading-id- "...")
  (datastore/load-safely (get-path reading-id-)
                         reading/is-valid?
                         #(do (reset! reading %)
                           (reset! reading-id reading-id-))))

(defn list- []
  (datastore/get-available-texts-ids (get-path)))

(defn get-metadata []
  (->
   @reading
   (dissoc :citations)
   (assoc :text-id @reading-id) ;; currently, this is to make sure the metadata-object is unique and therefore triggers a change of the page components
   ))

(defn get-citation-property [id prop]
  (-> @reading :citations (get id) (get prop)))

(defn update! [changeset]
  (update-safely! (reading/update- @reading changeset)))

(defn all []
  "
  Returns a sequence of sorted citation maps
  "
  (->> (:citations @reading)
       (sort-by #(-> % val :page js/parseFloat))
       (into [])
       (map (fn [[k v]] (assoc v :id k)))))

(defn delete-citation!
  [id]
  (-> @reading
      (reading/delete-citation id)
      (update-safely!)))

(defn create-citation! []
  (let [[updated-text new-key] (reading/create-citation @reading)]
    (update-safely! updated-text)
    new-key))