(ns rltb.scenario.scenario1
  (:require [rltb.scene :as scn])
  (:import (org.dyn4j.geometry Mass$Type)
           (org.dyn4j.dynamics Body)))

(def floor-niveau -26.7)
(def floor-thickness 30.0)

; rectangle-radius
(def rr 1.3)


; this is the param we want to optimize
(def model (atom 4.5))

(def falling-ball-handle (atom (Body.)))
(def floor-handle (atom (Body.)))


(defn improve
  []
  (swap! model (fn [_] (+ _ 0.5))))


(defn episode-ended?
  []
  (.isAsleep @falling-ball-handle))


(defn success?
  []
  (not (.isInContact @falling-ball-handle @floor-handle)))


(defn init
  []
  (scn/remove-bodies)

  (let [cannon-ball (scn/create-box
                      rr rr
                      4.0 (+ floor-niveau (/ floor-thickness 2) 3.0)
                      java.awt.Color/RED)
        resting-ball (scn/create-box
                       rr rr
                       14.0 (+ floor-niveau (/ floor-thickness 2) (/ rr 2))
                       java.awt.Color/BLACK)
        falling-ball (scn/create-box
                       rr rr
                       14.0 0.0
                       java.awt.Color/GREEN)
        floor (scn/create-box
                10000.0 floor-thickness
                0.0 floor-niveau
                java.awt.Color/BLACK Mass$Type/INFINITE)]

    (.setLinearVelocity cannon-ball @model 0.0)
    (.setAngularVelocity cannon-ball 2.5)
    (.setAngularVelocity falling-ball 5.5)

    (swap! floor-handle (fn [_] floor))
    (swap! falling-ball-handle (fn [_] falling-ball))
    (scn/add-bodies [cannon-ball resting-ball falling-ball floor])))