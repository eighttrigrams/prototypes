(defn write-dict [dictionary file-name]
  (spit file-name (into [] (map #(update % :date str) dictionary))))

(defn load-dict [file-name]
  (into []
        (->> (read-string (slurp file-name))
             (map
              #(if
                ; for test datasets
                (nil? (:date %))
                (assoc % :date (java.time.LocalDate/now))
                (update % :date (fn [entry] (java.time.LocalDate/parse entry))))))))