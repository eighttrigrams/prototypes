(ns melmac.context.writings
  (:require
    [melmac.model.writing :as writing]
    [melmac.config :as config]
    [melmac.datastore.filesystem :as datastore]))

(def writing-id (atom nil))
(def writing (atom {}))

(defn- get-path
  ([] (str config/store-path "writings/"))
  ([text-id] (str config/store-path "writings/" text-id)))

(defn- update-safely! [updated-text]
  (if (true? true) #_(not (reading/is-valid? updated-text)) #_(prn "Something went wrong")
    (datastore/update-safely! updated-text
                              (get-path @writing-id)
                              #(reset! writing updated-text))))

(defn update! [changeset]
  (update-safely! (writing/update- @writing changeset)))

(defn list- []
  (datastore/get-available-texts-ids (get-path)))

(defn initialize [writing-id-]
  (.log js/console "Initialize project" writing-id- "...")
  (datastore/load-safely (get-path writing-id-)
                         #(do true)
                         #(do
                           (reset! writing %)
                           (reset! writing-id writing-id-))))

(defn get-sections-property [id prop]
  (-> @writing :sections (get id) (get prop)))

(defn get-metadata []
  (-> @writing
      (dissoc :sections)
      (assoc :text-id @writing-id) ;; currently, this is to make sure the metadata-object is unique and therefore triggers a change of the page components
      ))

(defn all []
  "
  Returns a sequence of sorted section maps
  "
  (->> (:sections @writing)
       (sort-by #(-> % val :title js/parseFloat))
       (into [])
       (map (fn [[k v]] (assoc v :id k)))))

(defn delete-section!
  [id]
  (-> @writing
      (writing/delete-section id)
      (update-safely!)))

(defn create-section! []
  (let [[updated-text new-key] (writing/create-citation @writing)]
    (update-safely! updated-text)
    new-key))