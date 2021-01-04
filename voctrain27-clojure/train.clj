(defn update-entry [entry user-known]
  (let [user-knew?    (= user-known "y")
        new-entry     (update entry :phase (if user-knew? inc (constantly 1)))
        days-plus     (.plusDays (java.time.LocalDate/now)
                                 (if user-knew?
                                   (- (Math/pow 2 (:phase new-entry)) 2)
                                   0))]
    (assoc new-entry :date days-plus)))

(defn- train-step [dictionary [index dict-item]]
  (print "\n\n\n\nP" (:phase dict-item) "\n========================\ndata (e)ntry\n\n\n")
  (print (str (char 27) "[;H"))
  (print (str (:q dict-item) "? "))
  (flush)
  (def users-answer (read-line))
  (if (= users-answer "e")
    [dictionary true]
    (do
      (print "\n\n\nP" (:phase dict-item) "\n========================\n(y) for yes, other keys for no, (k) for kill\n\n\n")
      (print (str (char 27) "[;H"))
      (print (str (:q dict-item) "? " users-answer))
      (print (str "\n" (:a dict-item) "\n"))
      (print "\n")
      (print "Known? ")
      (flush)
      (def given-answer (read-line))
      [(into []
             (if (= given-answer "k")
               (filter #(not= dict-item %) dictionary)
               (assoc dictionary index (update-entry dict-item given-answer))))
       false])))

(defn should-show
  [[index dict-item]]
  (and
   (<= (:phase dict-item) 6)
   (<= (.compareTo (:date dict-item) (java.time.LocalDate/now)) 0)))

(defn zip-with-index [as]
  (map-indexed vector as))

(defn train [dictionary]
  (let [entries-to-show (into [] (filter should-show (zip-with-index dictionary)))]
    (if (empty? entries-to-show)
      (do (prn "no entries") (Thread/sleep 2000) [dictionary true])
      (train-step dictionary (get entries-to-show (rand-int (count entries-to-show)))))))