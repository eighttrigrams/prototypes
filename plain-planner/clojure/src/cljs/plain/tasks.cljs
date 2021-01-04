(ns plain.tasks
  (:require [plain.api :as api]
            [plain.people :as people]
            [plain.cards :as cards]
            [plain.model :as model]
            [plain.state :as state]
            [plain.util :as util]
            [reagent.core :as reagent :refer [atom]]))



(defn title-input [value key]
  [:input {:type "text"
           :placeholder "Add a title"
           :value (key @value)
           :on-change (fn [a] (do (swap! value assoc key (-> a  .-target .-value))))}])



(defn save [goal new-task]
  (let [updated-task (update new-task :time-remaining #(js/parseInt %))
        updated-goal (assoc goal :tasks (util/insert-into-items-with-ids (:tasks goal) updated-task))] ; todo insert into tasks of goal in backend, provide a specialized endpoint to do this and do not do this in the frontend
    (api/update-goal updated-goal)))



(defn card-remaining [not-editing new-task]
  [:<>
   [:div {:class :label} "Hours remaining: "]
   [:div
    (if not-editing
      (:time-remaining @new-task)
      [title-input new-task :time-remaining])]])



(defn card-description [not-editing new-task]
  [:<>
   [:div {:class :label} "Description: "]
   [:div
    (if not-editing
      (:description @new-task)
      [cards/atom-keyword-textarea new-task :description "Add a description"])]])



(defn- select-new-task [new-task new-task-target-value]
  (let [task-id (js/parseInt (.. new-task-target-value -target -value))
        task-for-id (model/get-item-for-id @state/people task-id)]
  (swap! new-task assoc :assignee task-for-id)))



(defn card-assignee [not-editing new-task]
  [:div
   [:div {:class :label} "Assignee: "]
   [:div
    (if not-editing
      (:name (:assignee @new-task))
      [:select
       {:on-change (partial select-new-task new-task)}
       (doall (map
          (fn [person]
            [:option
             {:value    (:id person)
              :key (str "select-person-" (:id person))
              :selected (= (:id (:assignee @new-task)) (:id person))}
             (:name person)])
          @state/people))])]])



(defn- task-card-body [new-task editing? goal]
  (let [not-editing (not editing?)]
  [:<>
     (card-assignee not-editing new-task)
     (card-remaining not-editing new-task)
     (card-description not-editing new-task)]))



(defn add-a-task [goal new-task-title]
  (let [g (update goal :tasks #(conj % {:title new-task-title
                                        :assignee "None"
                                        :time-remaining 0}))]
    (api/update-goal g)))



(defn task-card-wrapper [goal edited-item-id edit-or-save-function]
  (fn [idx task]
    (let [id                  (:id task)
          editable-task       (reagent/atom task)
          editing?            (= @edited-item-id id)
          save-goal!          #(save goal @editable-task)
          edit-or-save!       (partial edit-or-save-function save-goal! id)
          move-goal-upward!   #(api/move-task-upward goal @editable-task)
          rank-higher-partial (partial cards/rank-higher-button move-goal-upward! editing?)
          card-header-partial (partial cards/generic-card-header
                                       idx
                                       editable-task
                                       editing?
                                       edit-or-save!
                                       rank-higher-partial
                                       :title)]
      [:<>
       {:key (str "task-card-wrapper-" id)}
       [cards/generic-card
        card-header-partial
        #(task-card-body editable-task editing? goal)]
       false
       editing?])))



(defn tasks-card-list [goal task-card-wrapper-pt]
  [:div {:class :card-list}
   (doall (map-indexed task-card-wrapper-pt (:tasks goal)))
   (cards/plus-button #(api/make-task (:id goal)))])