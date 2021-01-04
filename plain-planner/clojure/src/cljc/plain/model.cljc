(ns plain.model)



(defn get-item-for-id [items-with-ids id]
  (let [filtered (filter #(= (:id %) id) items-with-ids)] ; todo make on combinator
    (if (empty? filtered)
      nil
      (first filtered))))



(defn convert-avails [availabilities]
  (map (fn [availability]
         (let [first (first availability)
               second (second availability)
               parsed (Integer/parseInt (str second))
               new-item [first parsed]]
           new-item)
         ) availabilities))



(defn transform-goals [goals tasks peoples] ; todo test
  "
  Transforms from the storage format of people, to the format
  which is served.
  "
  (let [on-id                           (fn [id] (partial filter #(= (:id %) id)))
        task-for-id                     (fn [task-id] (first ((on-id task-id) tasks)))
        assignee-for-id                 (fn [assignee-id] (first ((on-id assignee-id) peoples)))
        replace-assignee                #(-> % (update :assignee assignee-for-id))
        transform-tasks                 (partial map (comp replace-assignee task-for-id ))
        change-ids-for-tasks            #(update % :tasks transform-tasks)]
    (into [] (map change-ids-for-tasks goals))))




(defn- transform [goal]
  (let [transformed-tasks (into [] (map #(:id %) (:tasks goal)))]
    (assoc goal :tasks transformed-tasks)))



(defn make-goals-writable [goals]
  (let [transformed-goals (into [] (map transform goals))
        all-tasks (flatten (into [] (concat (map :tasks goals))))
        transformed-tasks (into [] (map #(update % :assignee :id) all-tasks))]
    [transformed-goals transformed-tasks]))



