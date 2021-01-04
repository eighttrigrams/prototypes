(ns plain.navbar
 (:require [plain.state :as state]
           [plain.api :as api]))


(defn- select-other-project [new-proj-target-value]
  (let [project (.. new-proj-target-value -target -value)]
    (reset! state/current-project project)
    (api/get-people)
    (api/get-goals)
    (api/get-timeline)))


(defn- project-select-dropdown []
 [:select
  {:on-change select-other-project}
  (doall (map
          (fn [project]
           [:option
            {:value    project
             :key (str "select-project-" project)
             :selected (= @state/current-project project)}
            project])
          @state/projects))])



(defn wrap-nav [content]
  [:div {:class :container}
   [:div {:class :row :id :nav-bar}
    [:div
     {:class :col-md-12}
     [:div
      {:class :left-side-menu}
      [:h1 (:text "Planner")]
      [:a {:href "#/"} "Timeline"]
      [:span " | "]
      [:a {:href "#/goals"} "Goals"]
      [:span " | "]
      [:a {:href "#/people"} "People"]]
     [:div
      {:class "right-side-menu" :style {:position :absolute :right 12 :top 8}}
      (project-select-dropdown)]]]
   content])