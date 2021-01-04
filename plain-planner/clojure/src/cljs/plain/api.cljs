(ns plain.api
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [plain.state :as state]
            [cljs-http.client :as http]))


(defn get-timeline []
  (go
   (let [response (<! (http/get (str "/timeline/" @state/current-project "?from=230&days=365")))]
     (reset! state/timeline (:body response)))))



(defn make-goal []
  "
  #title - the title of the goal
  "
  (go
   (let [response (<! (http/post (str "/goals/" @state/current-project)))]
     (reset! state/goals (:goals (:body response))))))



(defn make-task [goal-id]
  "
  #title - the title of the goal
  "
  (go
   (let [response (<! (http/post (str "/tasks/" @state/current-project "/" goal-id)))]
     (reset! state/goals (:goals (:body response))))))



(defn update-person [person]
  (go
   (let [response (<! (http/put (str "/people/" @state/current-project) {:json-params person}))]
     (reset! state/people (:people (:body response))))))



(defn update-goal [goal]
  "
  #title - the title of the goal
  "
  (go
   (let [response (<! (http/put (str "/goals/" @state/current-project) {:json-params goal}))]
     (reset! state/goals (:goals (:body response))))))



(defn delete-goal [goal]
  (go
   (let [response (<! (http/delete (str "/goals/" @state/current-project "/" (:id goal))))]
     (reset! state/goals (:goals (:body response))))))



(defn get-goals []
  (go
   (let [response (<! (http/get (str "/goals/" @state/current-project)))]
     (reset! state/goals (:goals (:body response))))))



(defn post-person [name]
  "
  #name - the name of the person
  "
  (go
   (let [response (<! (http/post (str "/people/" @state/current-project) {:json-params {:name name}}))]
     (reset! state/people (:people (:body response))))))



(defn get-people []
  (go
   (let [response (<! (http/get (str "/people/" @state/current-project)))]
     (reset! state/people (:people (:body response))))))



(defn get-projects []
  (go
   (let [response (<! (http/get "/projects"))]
     (reset! state/projects (:projects (:body response))))))



(defn move-goal-upward [goal]
  (go
   (let [response (<! (http/post (str "/goals/" @state/current-project "/up/" (:id goal))))]
     (reset! state/goals (:goals (:body response))))))



(defn move-task-upward [goal task]
  (go
   (let [response (<! (http/post (str "/tasks/" @state/current-project "/up/" (:id goal) "/" (:id task))))]
     (reset! state/goals (:goals (:body response))))))