(ns plain.timeline
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [plain.navbar :as navbar]
            [plain.state :as state]
            [reagent.core :as reagent :refer [atom]]
            [cljs-http.client :as http]
            [clojure.string :as string]))


(def day-width 40)

(def max-working-hrs-a-day 12.00)

(def all-months ["January" "February" "March" "April" "May" "June" "July" "August" "September" "October" "November" "December"])

(def day-height "50px") ; todo remove


(defn- make-task-elem
  [person-name day-idx]
  (fn [task]
    (let [task-duration-hrs         (:time task)
          goal-title                (:goal task)
          task-title                (:title task)
          color-class               (if (= goal-title "Goal 2") "even" "odd")] ; todo un-hardcode

      [:div
       {:key   (str person-name "-day-" day-idx task-title) ; todo improve
        :title task-title
        :data-toggle :tooltip
        :class (str "float-left task-elem " color-class)
        :style {:width            (str (* day-width (/ task-duration-hrs max-working-hrs-a-day)) "px")}}
       task-duration-hrs])))



(defn- make-elem-for-day
  [person-name]
  (fn [day-idx day]
    (let [tasks                       (:tasks day)
          time-for-tasks              (reduce + (map :time tasks))
          time-remaining-for-week     (:time-remaining day)
          time-remaining-to-full-week (- 12 time-for-tasks time-remaining-for-week)
          time-to-fill                (+ time-remaining-for-week time-remaining-to-full-week)]
      [:div
       {:key (str person-name "-day-" day-idx) :class :item}
       (concat (map (make-task-elem person-name day-idx) tasks)
               [[:div
                 {:key   (str person-name "-day-" day-idx "-" "free")
                  :class :float-left
                  :style {:position         :relative
                          :background-color "#F4F6F6"
                          :height           day-height
                          :width            (str (* day-width (/ time-to-fill max-working-hrs-a-day)) "px")}}]])])))



(defn- calc-heading-color [dow]
  (cond
    (= dow "MONDAY")    :lightslategray
    (= dow "TUESDAY")   :lightseagreen
    (= dow "WEDNESDAY") :lightslategray
    (= dow "THURSDAY")  :lightseagreen
    (= dow "FRIDAY")    :lightslategray
    (= dow "SATURDAY")  :white
    (= dow "SUNDAY")    :white
    :else               :black))



(defn- get-month-name [month]
  (nth all-months (- month 1)))



(defn- headings [timeline]
  (into [:<>]
        (map-indexed
          (fn [idx [dow date]]
            (let [month                          (js/parseInt (string/join (take 2 (drop 3 date))))
                  month-name                     (get-month-name month)
                  month-first-letter             (first dow)
                  month-digits                   (take 2 date)
                  day                            (js/parseInt (string/join (take 2 date)))
                  month-background-color         (if (= (mod month 2) 1) :lightgray :gray)
                  should-display-month-indicator (or (= day 1) (= idx 0))]

              [:div {:class :timeline-heading-item
                     :style {:background-color (calc-heading-color dow)}}
               (when should-display-month-indicator [:span {:class :month-indicator} month-name])
               [:div {:class :letter} month-first-letter]
               [:div {:class :digit}  month-digits]
               [:div {:class :month
                      :style {:background-color month-background-color}}]]))
          timeline)))



(defn page []
  (fn []
    (navbar/wrap-nav
     [:div
      [:div {:class :section-heading} "Timeline"]
      [:div
       {:class "timeline-container page"}
       [:div
        {:class :the-timeline}
        (let [timel @state/timeline]
          [:<>
           [:div {:class :timeline-heading} (headings (:heading timel))]
           [:br]
           [:br]
           (->> timel
                (filter (fn [[person days]] (not= person :heading)))
                (map
                 (fn [[person days]]
                   [:div
                    {:key (str person "-timeline-!-")}
                    [:h5 person]
                    [:div (map-indexed (make-elem-for-day person) days)]
                    [:br]
                    [:br]
                    [:br]])))])]]])))
