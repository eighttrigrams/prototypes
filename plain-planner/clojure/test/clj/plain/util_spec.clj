(ns plain.util_spec
  (:use clojure.test)
  (:require [plain.util :as util]))


(deftest move-upward-test
  (let [goal1 {:title "Goal1" :tasks ["t1" "t2"]}
        goal2 {:title "Goal2" :tasks ["t3" "t4"]}
        goals [goal2 goal1]
        swapped-goals (util/move-upward goals goal1)]
    (is (= swapped-goals [goal1 goal2]))))



(deftest move-upward-test--already-on-first-position
  (let [goal1 {:title "Goal1" :tasks ["t1" "t2"]}
        goal2 {:title "Goal2" :tasks ["t3" "t4"]}
        goals [goal1 goal2]
        swapped-goals (util/move-upward goals goal1)]
    (is (= swapped-goals [goal1 goal2]))))



(deftest move-upward-test--goal-to-move-does-not-exist
  (let [goal1 {:title "Goal1" :tasks ["t1" "t2"]}
        goal2 {:title "Goal2" :tasks ["t3" "t4"]}
        goal3 {:title "Goal3" :tasks ["t5" "t6"]}
        goals [goal2 goal1]
        swapped-goals (util/move-upward goals goal3)]
    (is (= swapped-goals [goal2 goal1]))))



(deftest move-upward-test--goal-is-nil
  (let [goal1 {:title "Goal1" :tasks ["t1" "t2"]}
        goal2 {:title "Goal2" :tasks ["t3" "t4"]}
        goals [goal1 goal2]
        swapped-goals (util/move-upward goals nil)]
    (is (= swapped-goals [goal1 goal2]))))




(def goals [{:title "Goal 1"
                     :id 1
                     :tasks [{:title "Task 1a"} {:title "Task 1b"}]}
                    {:title "Goal 2"
                     :id 2
                     :tasks [{:title "Task 2a"} {:title "Task 2b"}]}])


(def expected [{:title "Goal 1"
                :id 1
                :tasks [{:title "Task 1a"} {:title "Task 1b"}]}
               {:title "Goal 2"
                        :id 2
                        :tasks [{:title "Task 2a"} {:title "Task 2b"} {:title "Task 2c"}]}
                       ])


(deftest insert-into-goals-test
  (let [goal {:title "Goal 2"
              :id 2
              :tasks [{:title "Task 2a"} {:title "Task 2b"} {:title "Task 2c"}]}]
    (is (= expected (util/insert-into-items-with-ids goals goal)))))