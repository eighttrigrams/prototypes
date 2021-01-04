(ns rltb.gameloop
  (:gen-class
    :init init
    :implements [java.awt.event.ActionListener])
  (:require
    config
    rltb.scene-painter
    rltb.scene)
  (:import (java.time Instant Duration)))


(def last-time (atom (Instant/now)))

(def initialized (atom false))

(def success-message-shown (atom false))


(defn- -init []
  (require (symbol (str config/scenario))))


(defn scenario-init []
  ((find-var (symbol config/scenario "init"))))


(defn- scenario-success? []
  ((find-var (symbol config/scenario "success?"))))


(defn- scenario-improve []
  ((find-var (symbol config/scenario "improve"))))


(defn- scenario-episode-ended? []
  ((find-var (symbol config/scenario "episode-ended?"))))


(defn- swaptrue!
  [bool-atom] (swap! bool-atom (fn [_] true)))


(defn step
  []
  (let
    [time-elapsed (.toSeconds (Duration/between @last-time (Instant/now)))]
    (if (not (scenario-episode-ended?))
      (do
        (rltb.scene/up)
        (rltb.scene-painter/paint-it (rltb.scene/get-bodies)))
      (do
        (if (true? (scenario-success?))

          (if (false? @success-message-shown)
            (do (prn "success")
                (swaptrue! success-message-shown)))

          (do (scenario-improve)
              (scenario-init)))
        (swap! last-time (fn [_] (Instant/now)))))))


(defn -actionPerformed
  [this event]
  (if (false? @initialized)
    (do
      (scenario-init)
      (swaptrue! initialized)))
    (step))

