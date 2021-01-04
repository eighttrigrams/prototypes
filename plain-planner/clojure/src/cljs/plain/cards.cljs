(ns plain.cards
  (:require [plain.api :as api]
            [reagent.core :as reagent :refer [atom]]))


(defn atom-keyword-input [atom keyword placeholder]
  [:input {:type "text"
           :placeholder placeholder
           :value (keyword @atom)
           :on-change (fn [a] (do (swap! atom assoc keyword (-> a .-target .-value))))}])


(defn atom-keyword-textarea [atom keyword placeholder]
  [:textarea
   {:type        "text"
    :placeholder placeholder
    :value       (keyword @atom)
    :on-change (fn [a] (do (swap! atom assoc keyword (-> a .-target .-value))))}])



(defn mat-icon [which] [:span {:class "material-icons"} which])



(defn edit-or-save-fn [editing-id-atom]
  (fn [on-save id save?]
    (if save?
      (do
        (reset! editing-id-atom -1)
        (on-save))
      (reset! editing-id-atom id))))



(defn plus-button [on-click]
  [:div
   {:class :material-icons
    :style {:display :inline-block}
    :on-click on-click} "add_circle"])



(defn delete-button [on-click]
  (let [base-classes "rank-higher-button goal-card-button"
        classes      (str base-classes " inner-container activatable")]
    [:div
     {:class    classes
      :on-click on-click}
     (mat-icon "delete_forever")]))



(defn edit-done-button [edit-or-save! editing? cursor-in-card-atom]
  (let [base-classes "goal-card-button edit-and-done-button"
        classes      (str base-classes " inner-container activatable")]
    (if (or @cursor-in-card-atom editing?)
      (if editing?
        [:div
         {:class    classes
          :on-click #(edit-or-save! true)}
         (mat-icon "done")]
        [:div
         {:class    classes
          :on-click #(edit-or-save! false)}
         (mat-icon "create")])
      [:div
       {:class base-classes}])))



(defn rank-higher-button [move-goal-upward! editing? cursor-in-card idx]
  (let [base-classes "rank-higher-button goal-card-button"
        classes      (str base-classes " inner-container activatable")]
    (if (and cursor-in-card (not editing?) (not (= idx 0)))
      [:div
       {:class    classes
        :on-click move-goal-upward!}
       (mat-icon "arrow_upward")]
      [:div {:class base-classes}])))



(defn generic-card-header
  [idx
   item-atom
   editing?
   edit-or-save!
   left-side-button-pt
   path
   cursor-in-card-atom]
  "
  "
  (let [title                (path @item-atom)]
    [:<>
     (left-side-button-pt @cursor-in-card-atom idx)
     [:div
      {:class (str "title-button inner-container" (when editing? " editing"))}
      (if-not editing?
        [:div {:class :title} title]
        [atom-keyword-input item-atom path "Add a title"])]
     (edit-done-button edit-or-save! editing? cursor-in-card-atom)]))



(defn generic-card [header-partial body-partial on-click! selected? editing?]
  ""
  (let [cursor-in-card    (reagent/atom false)]
    [:div
     {:class          (str "card" (when (and selected? (not editing?)) " selected")) ; todo pull out selected? and editing?
      :on-mouse-over  #(reset! cursor-in-card true)
      :on-mouse-leave #(reset! cursor-in-card false)
      :on-click       on-click!}
     [:div
      {:class :card-header}
      [:h5
       {:class :card-title}
       [header-partial cursor-in-card]]]
     [:div {:class :card-body} (body-partial)]]))