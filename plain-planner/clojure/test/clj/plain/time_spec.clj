(ns plain.time-spec
  (:import (java.util Date)
           (java.time LocalDate)
           (java.time.temporal ChronoUnit))
  (:use clojure.test)
  (:require [plain.time :as time]))



(deftest test-to-number--one-day
  (let [result (time/convert-german-date-to-number "02.01.2019")]
    (is (= result 1))))



(deftest test-to-number--one-year
  (let [result (time/convert-german-date-to-number "01.01.2020")]
    (is (= result 365))))



(deftest test-to-number--two-years ; with Schaltjahr
  (let [result (time/convert-german-date-to-number "01.01.2021")]
    (is (= result (+ 1 (* 365 2))))))



(deftest get-weekday-from-date-number
  (is (= "MONDAY" (time/get-date-from (time/convert-german-date-to-number "19.08.2019")))))