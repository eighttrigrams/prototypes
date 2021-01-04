(ns rltb.scenario.scenario2
  (:import (org.dyn4j.geometry Rectangle Mass$Type)
           (org.dyn4j.dynamics Body World)))

(def floor-niveau -26.7)
(def floor-thickness 30.0)

; rectangle-radius
(def rr 1.3)


; this is the param we want to optimize
; when it goes to 8.5 the green ball does not hit the ground
; but comes to rest on top of the red ball. so we will punish
; the model when the green hits the ground.
(def model (atom 10.5))

(def green-ball (atom (Body.)))
(def floor (atom (Body.)))


(defn improve
  []
  (swap! model (fn [_] (- _ 0.5))))


(defn success?
  []
  (if (.isInContact @green-ball @floor) \f \t))


(defn init [])