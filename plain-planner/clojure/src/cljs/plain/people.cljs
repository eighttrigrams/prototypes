(ns plain.people
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [plain.navbar :as navbar]
            [plain.api :as api]
            [plain.state :as state]
            [plain.cards :as cards]
            [plain.model :as model]
            [plain.person-timetable :as person-timetable]
            [clojure.string :as str]
            [reagent.core :as reagent :refer [atom]]
            [cljs-http.client :as http]))



(defn- person-card-body [id]
  [:div {:key (str "person-card-body-" id)} "..."])



(defn- person-card-wrapper [selected-person-id edited-person-id edit-or-save-function]
  (fn [idx person]
    (let [id                   (:id person)
          editable-person      (reagent/atom person)
          editing?             (= id @edited-person-id)
          selected?            (= id @selected-person-id)
          select!               #(reset! selected-person-id id)
          rank-higher-button   (fn [move-goal-upward! editing? cursor-in-card idx] [:div {:key (str "rank-higher-" id)}])
          update-person!       #(api/update-person @editable-person)
          edit-or-save!        (partial edit-or-save-function update-person! id)
          card-header-partial  (partial cards/generic-card-header
                                        idx
                                        editable-person
                                        editing?
                                        edit-or-save!
                                        rank-higher-button
                                        :name)]
      [:<> {:key (str "person-card-wrapper- " id)}
       [cards/generic-card
        card-header-partial
        #(person-card-body id)
        select!
        selected?
        editing?]])))



(defn people-card-list [selection-id editing-id edit-or-save-fn]
  [:div {:class :card-list}
   (doall (map-indexed (person-card-wrapper selection-id editing-id edit-or-save-fn) @state/people))
   (cards/plus-button #(api/post-person ""))])



(defn page []
  (let [edited-person-id      (atom -1)
        selected-person-id    (atom 0)
        edit-or-save-fn       (fn [on-save id save?] ; todo remove redundant code
                                (if save?
                                  (do
                                    (reset! edited-person-id -1)
                                    (on-save))
                                  (do
                                    (reset! edited-person-id id))))]
    (fn []
      (navbar/wrap-nav
       [:div
        {:class :row}
        [:div
         {:class :col-md-6}
         [:div {:class :section-heading} "People"]
         (people-card-list selected-person-id edited-person-id edit-or-save-fn)]
        [:div
         {:class :col-md-6}
         (let [peoples @state/people]
           (if (> (count peoples) 0)
             (let [selected (model/get-item-for-id peoples @selected-person-id)]
               [:<>
                [:div
                 {:class :section-heading}
                 (str (:name selected) " | Timetable")]
                (person-timetable/wrap-avails selected)])
             [:div {:class :section-heading} "Select a person first"]))]]))))



