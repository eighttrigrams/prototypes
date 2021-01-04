(ns plain.handler
  (:require
    [clojure.walk :as walk]
    [plain.data :as data]
    [plain.util :as util]
    [plain.model :as model]))



(defn- timeout []

  "Just to test how the frontend reacts to delays"

  (comment (prn "get request - wait a bit")
  (Thread/sleep 2000)
  (prn "get request - answer now")))



(defn- make-goals-body [project]
  {:goals (deref (project @data/goals))})



(defn- make-people-body [project]
  {:people (deref (project @data/people))})



(defn- people-ok-response [project]
  {:status 200
   :body (make-people-body project)})



(defn- goals-ok-response [project]
  {:status 200
   :body (make-goals-body project)})



(defn get-people [project]
  (timeout)
  (people-ok-response project))



(defn make-person [project]
  (let [person {:id (data/generate-id) :name "" :availability []}]
    (swap! (project @data/people) #(into [] (concat % [person]))) ; todo make somthing like swap assoc
    (timeout)
    (people-ok-response project)))



(defn update-person [project person]
  (let [avails-with-parsed-nums (-> person :availability model/convert-avails)
        updated-person (assoc person :availability avails-with-parsed-nums)
        updated-people (util/insert-into-items-with-ids (deref (project @data/people)) updated-person)]
    (reset! (project @data/people) updated-people)
    (timeout)
    (people-ok-response project)))



(defn get-goals [project]
  (timeout)
  (goals-ok-response project))



(defn get-projects []
  (timeout)
  {:status 200
   :body {:projects (into [] (data/get-project-names))}})



(defn delete-goal [project id]
  (let [new-goals (into [] (filter #(not= (:id %) (Integer/parseInt id)) (deref (project @data/goals))))]
    (reset! (project @data/goals) new-goals)
    (timeout)
    (goals-ok-response project)))



(defn make-goal [project]
  (let [goal {:id (data/generate-id)}]
    (swap! (project @data/goals) #(into [] (concat % [goal])))
    (timeout)
    (goals-ok-response project)))



(defn make-task [project goal-id]
  (let [goal                     (model/get-item-for-id (deref (project @data/goals)) (Integer/parseInt goal-id))
        goal-idx                 (.indexOf (deref (project @data/goals)) goal)
        tasks                    (:tasks goal)
        task                     {:id (data/generate-id) :time-remaining 1}
        new-tasks                (concat tasks [task])
        goal-with-new-tasks      (assoc goal :tasks new-tasks)]
    (swap! (project @data/goals) assoc goal-idx goal-with-new-tasks)
    (timeout)
    (goals-ok-response project)))



(defn put-goal [project goal]
  (let [tasks                    (filter #(> (:time-remaining %) 0) (:tasks goal))
        goal-with-filtered-tasks (assoc goal :tasks tasks)]
    (swap! (project @data/goals) #(util/insert-into-items-with-ids % goal-with-filtered-tasks))
    (goals-ok-response project)))



(defn move-goal-upward [project id]
  (let [goal  (model/get-item-for-id (deref (project @data/goals)) (Integer/parseInt id))
        goals (util/move-upward (deref (project @data/goals)) goal)]
    (reset! (project @data/goals) goals)
    (goals-ok-response project)))



(defn move-task-upward [project goal-id task-id]
  (let [goal                     (model/get-item-for-id (deref (project @data/goals)) (Integer/parseInt goal-id))
        goal-idx                 (.indexOf (deref (project @data/goals)) goal)
        goal-tasks               (into [] (map identity (:tasks goal))) ; todo check why this isn't already a vector
        task                     (model/get-item-for-id goal-tasks (Integer/parseInt task-id))
        tasks                    (util/move-upward goal-tasks task)
        goal-with-replaced-tasks (assoc goal :tasks tasks)]
    (swap! (project @data/goals) assoc goal-idx goal-with-replaced-tasks)
    (goals-ok-response project)))


