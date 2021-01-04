(ns plain.timeline-spec
  (:use clojure.test)
  (:require [plain.handler :as handler]
            [plain.timeline :as timeline]))



(def peoples [{:name         "Daniel"
               :id           1
               :availability [["01.01.2019" 10] ; tag / stunden
                              ; second day not specified
                              ["03.01.2019" 10]
                              ; fourth day not specified
                              ["05.01.2019" 10]
                              ["06.01.2019" 10]]}])



(def goals [{:title "Goal 1"
             :tasks [{:title          "Task 1a"
                      :assignee       {:id 1}
                      :time-remaining 11}
                     {:title          "Task 1b"
                      :assignee       "Tobi"
                      :time-remaining 12}]}
            {:title "Goal 2"
             :tasks [{:title          "Task 2a"
                      :assignee       {:id 1}
                      :time-remaining 13}]}])




(deftest timeline-test
  (let [timeline (timeline/make-timeline peoples goals 1 4)
        daniels-timeline (:Daniel timeline)
        first-week-tasks (:tasks (nth daniels-timeline 0))
        second-week-tasks (:tasks (nth daniels-timeline 1))
        third-week-tasks (:tasks (nth daniels-timeline 2))
        fourth-week-tasks (:tasks (nth daniels-timeline 3))]

    (is (= (count first-week-tasks) 0))
    
    (is (= (count second-week-tasks) 1))
    (let [second-week-first-task (nth second-week-tasks 0)]
      (is (= (:title second-week-first-task) "Task 1a"))
      (is (= (:goal second-week-first-task) "Goal 1"))
      (is (= (:time second-week-first-task) 10)))

    (is (= (count third-week-tasks) 0))

    (is (= (count fourth-week-tasks) 2))
    (let [fourth-week-first-task (nth fourth-week-tasks 0)
          fourth-week-second-task (nth fourth-week-tasks 1)]
      (is (= (:title fourth-week-first-task) "Task 1a"))
      (is (= (:goal fourth-week-first-task) "Goal 1"))
      (is (= (:time fourth-week-first-task) 1))
      (is (= (:title fourth-week-second-task) "Task 2a"))
      (is (= (:goal fourth-week-second-task) "Goal 2"))
      (is (= (:time fourth-week-second-task) 9)))))
