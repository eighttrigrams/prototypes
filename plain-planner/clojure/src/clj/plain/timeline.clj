(ns plain.timeline
  (:require [plain.time :as time]))



(defn- goal-is-for-person? [person-id]
  (fn [goal]
    (let [tasks (:tasks goal)
          id person-id
          assignees (into [] (map (fn [task] (:id (:assignee task))) tasks))]
      (not (nil? (some #{id} assignees))))))



(defn- get-tasks-for-person [person-id]
  (fn [goal]
    (let [tasks (:tasks goal)
          cleaned-tasks (into [] (filter (fn [task] (= person-id (:id (:assignee task)))) tasks))
          tasks-with-assignees-removed (into [] (map #(dissoc % :assignee) cleaned-tasks))]
      (assoc goal :tasks tasks-with-assignees-removed))))



(defn- put-goal-into-task [goal]
  (let [tasks (:tasks goal)
        title (:title goal)]
    (into [] (map #(assoc % :goal title) tasks))))



(defn- insert-task-into-current-day [current-day
                                     task
                                     assoc-days
                                     current-day-index
                                     time-remaining-after-task-done]
  "
  "
  (let [current-day-with-new-tasks
        (->>
         (conj (:tasks current-day) (assoc task :time (:time-remaining task)))
         (assoc current-day :tasks))
        new-days
        (->>
         (assoc current-day-with-new-tasks :time-remaining time-remaining-after-task-done)
         assoc-days)
        new-day-index
        (if (> time-remaining-after-task-done 0)
          current-day-index
          (inc current-day-index))]
    [new-days new-day-index]))



(defn- split-task-between-days [current-day
                                task
                                assoc-days
                                current-day-index
                                make-days]

  (let [diff                        (- (:time-remaining task) (:time-remaining current-day))

        current-day-with-new-tasks  (->>
                                     (assoc (assoc task :time-remaining 0) :time (:time-remaining current-day))
                                     (conj (:tasks current-day))
                                     (assoc current-day :tasks))

        new-days                    (->> (assoc current-day-with-new-tasks :time-remaining 0)
                                         assoc-days)
        ]

    (->> (assoc task :time-remaining diff)
         (make-days [new-days (inc current-day-index)]))))



(defn- make-days [start-day number-of-days]
  "
  "
  (fn [[days current-day-index] task]
    "
    "
    (let [offset (- current-day-index start-day)]
    (if (< offset number-of-days)
      (let [offset (- current-day-index start-day)
            current-day                    (nth days offset)
            time-remaining-after-task-done (- (:time-remaining current-day) (:time-remaining task))
            assoc-days (partial assoc days offset)]

        (cond
          (= 0 (:time-remaining current-day))
          ((make-days start-day number-of-days) [days (inc current-day-index)] task)
          (<= (:time-remaining task) (:time-remaining current-day))
          (insert-task-into-current-day current-day task assoc-days current-day-index time-remaining-after-task-done)
          :else
          (split-task-between-days current-day task assoc-days current-day-index (make-days start-day number-of-days))))
      [days current-day-index]))))



(defn- convert-availabilities-to-full-days [availability start-day number-of-days]
  "
  "
  (reduce
   (fn [days i]

     (let [found (filter (fn [[day v]] (= (time/convert-german-date-to-number day) i)) availability)]
       (conj days (if (empty? found)
         {:time-remaining 0 :tasks []}
         {:time-remaining (second (first found)) :tasks [] }))))

   [] (range start-day (+ start-day number-of-days))))



(defn- make-timeline-for-person [goals start-day number-of-days]
  (fn [person]
    (let [name            (:name person)
          person-id       (:id person)
          availability    (:availability person)
          days-for-person (convert-availabilities-to-full-days availability start-day number-of-days)
          days            (->> goals
                               (filter (goal-is-for-person? person-id))
                               (map (get-tasks-for-person person-id))
                               (map put-goal-into-task)
                               flatten
                               (reduce (make-days start-day number-of-days) [days-for-person start-day])
                               first)]
      {(keyword name) days})))



(defn make-timeline-heading [start-day number-of-days]
  "
  "
  (reduce (fn [days i]
            (let [dow (time/get-date-from i)
                  date (time/format-number i)]
              (conj days [dow date]))
            ) [] (range start-day (+ start-day number-of-days))) )



(defn make-timeline [people goals start-day number-of-days]
  (let [peoples-timeline (->>
                          (map
                           (make-timeline-for-person goals start-day number-of-days)
                           people)
                          (into {}))]
    (assoc peoples-timeline :heading (make-timeline-heading start-day number-of-days))))