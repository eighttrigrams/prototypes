(ns plain.goals
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [plain.navbar :as navbar]
            [plain.people :as people]
            [plain.api :as api]
            [plain.state :as state]
            [plain.cards :as cards]
            [plain.model :as model]
            [plain.tasks :as tasks]
            [reagent.core :as reagent :refer [atom]]))


(def label {:class :label})

(def card-list {:class :card-list})



(defn- goal-card-body [goal-atom editing?]
  [:<>
   [:div label "Due date: "]
   (if-not editing?
      (:due-date @goal-atom)
      [cards/atom-keyword-input goal-atom :due-date "Add a date"])
   [:div label "Description: "]
   (if-not editing?
      (:description @goal-atom)
      [cards/atom-keyword-textarea goal-atom :description "Add a description"])])



(defn- move-or-delete-button [goal editing? cursor-in-card? idx]
  (if (and editing? (< (count (:tasks @goal)) 1))
    (cards/delete-button #(api/delete-goal @goal))
    (cards/rank-higher-button #(api/move-goal-upward @goal) editing? cursor-in-card? idx)))



(defn- goal-card-wrapper [selection-id-atom editing-id-atom edit-or-save-fn idx goal]
  (let [id                                    (:id goal)
        editing?                              (= @editing-id-atom id)
        selected?                             (= @selection-id-atom id)
        editable-goal                         (reagent/atom goal)
        select!                               #(reset! selection-id-atom id)
        put-goal!                             #(api/update-goal @editable-goal)
        edit-or-save!                         (partial edit-or-save-fn put-goal! id)
        left-hand-side-button-partial         (partial move-or-delete-button editable-goal editing?)
        card-header-partial                   (partial cards/generic-card-header
                                                       idx
                                                       editable-goal
                                                       editing?
                                                       edit-or-save!
                                                       left-hand-side-button-partial
                                                       :title)]
    [:<>
     {:key (str "goals-card-wrapper- " id)}
     [cards/generic-card
      card-header-partial
      #(goal-card-body editable-goal editing?)
      select!
      selected?
      editing?]]))



(defn- goals-card-list [selected-goal-id edited-item-id edit-or-save-fn]
  [:div card-list
   (doall (map-indexed (partial goal-card-wrapper selected-goal-id edited-item-id edit-or-save-fn) @state/goals))
   (cards/plus-button #(api/make-goal))])



(defn- tasks-card-list-heading [selected tasks-count]
  (let [plural?              (not= tasks-count 1)]
    [:div
     {:class :section-heading}
     (str (if (empty? (:title selected)) "No Title" (:title selected))
          " | "
          (if (= tasks-count 0) "No" tasks-count) " Task" (when plural? "s"))]))



(defn- right-hand-side [selected-goal-id edited-item-id edit-or-save-fn]
  (let [selected             (model/get-item-for-id @state/goals @selected-goal-id)
        tasks-count          (count (:tasks selected))
        task-card-wrapper-pt (tasks/task-card-wrapper selected edited-item-id edit-or-save-fn)]
    [:div {:class :col-md-8}
     (tasks-card-list-heading selected tasks-count)
     [tasks/tasks-card-list selected task-card-wrapper-pt]]))



(defn- alternative-right-hand-side []
  [:div {:class :col-md-8}
   [:div {:class :section-heading}
    "Create a goal first. then you can add tasks here ..."]])



(defn- page-contents [selection-id-atom editing-id-atom goals-exist]
  (let [edit-or-save-fn (cards/edit-or-save-fn editing-id-atom)]
    [:div {:class :row}
     [:div {:class :col-md-4}
      [:div {:class :section-heading} "Project | Goals"]
      (goals-card-list selection-id-atom editing-id-atom edit-or-save-fn)]
     (if goals-exist
       (right-hand-side selection-id-atom editing-id-atom edit-or-save-fn)
       (alternative-right-hand-side))]))



(defn page []
  (let [selected-goal-id (atom 1)
        edited-item-id   (atom -1)]
    (fn []
      (let [goals-exist (not (empty? @state/goals))]
        (navbar/wrap-nav (page-contents selected-goal-id edited-item-id goals-exist))))))