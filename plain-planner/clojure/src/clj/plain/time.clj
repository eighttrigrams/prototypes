(ns plain.time
  (:import (java.util Date)
           (java.time LocalDate)
           (java.time.temporal ChronoUnit)
           (java.time.format DateTimeFormatter)))


(defn- time-diff [to-date]
  (let [fromDateTime (LocalDate/of 2019 1 1)]
    (.until fromDateTime to-date ChronoUnit/DAYS)))



(defn get-date-from [day-number]
  (let [date-time (LocalDate/of 2019 1 1)
        new-date-time (.plusDays date-time day-number)
        ]
    (.name (.getDayOfWeek new-date-time))))


(defn convert-german-date-to-number [given-day]
  "
  1.1.2019 -> 0
  2.1.2019 -> 1
  etc.
  "
  (let [formatter (DateTimeFormatter/ofPattern "dd.MM.yyyy")
        to-date   (LocalDate/parse given-day formatter)]
    (time-diff to-date)))


(defn format-number [num]
  (let [formatter (DateTimeFormatter/ofPattern "dd.MM.yyyy")
        date-time (LocalDate/of 2019 1 1) ; todo dedup
        new-date-time (.plusDays date-time num)]
    (.format new-date-time formatter)))