(ns plain.person-timetable
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [plain.navbar :as navbar]
            [plain.api :as api]
            [clojure.string :as str]
            [reagent.core :as reagent :refer [atom]]
            [cljs-http.client :as http]))



(defn save! [person new-availabilities]
  (let [filtered-availabilities (into [] (filter #(not (empty? %)) new-availabilities))
        splitted-availabilities (into [] (map #(str/split % ";") filtered-availabilities))
        updated-person (assoc person :availability splitted-availabilities)]
    (api/update-person updated-person)))



(defn edit-button [edit-mode new-availabilities person]
  (let [class "material-icons"]
    (if (not @edit-mode)
      [:span {:class class
              :on-click #(reset! edit-mode true)} "create"]
      [:span {:class class
              :on-click #(do
                          (save! person @new-availabilities)
                          (reset! edit-mode false))} "done"])))



(defn simple-input [value key]
  [:input {:type "text"
           :placeholder "Add an entry"
           :value (nth @value key)
           :on-change (fn [a]
                        (do (swap! value assoc key (-> a  .-target .-value))))}])



(defn display-availabilities [person new-availabilities edit-mode]
  [:div {:class :person-timetable}
   [:div {:style {:text-align :center}}
    (edit-button edit-mode new-availabilities person)
    [:br]
    [:br]
    [:<>
     (doall (map-indexed (fn [idx el]
                    (if @edit-mode
                      [:div {:key (str "availability-person-" (:id person) "-" idx)} [simple-input new-availabilities idx]]
                      [:div {:key (str "availability-person-" (:id person) "-" idx)} el])) @new-availabilities))]
    [:br]
    [:div
     {:class :material-icons
      :style {:display :inline-block}
      :on-click #(let [altered-availabilities (into [] (concat @new-availabilities ["01.01.2019;1"]))]
                  (save! person altered-availabilities)
                  (reset! new-availabilities altered-availabilities))} "add_circle"]]])



(defn wrap-avails [selected-person]
  (let [edit-mode (reagent/atom false)
        new-availabilities
        (reagent/atom (into [] (map (fn [[desc, time]] (str desc ";" time)) (:availability selected-person))))]
    [display-availabilities
     selected-person
     new-availabilities
     edit-mode]))