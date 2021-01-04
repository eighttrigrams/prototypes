(ns rltb.scene
  (:import (org.dyn4j.dynamics Body World)
           (org.dyn4j.geometry Mass$Type Rectangle))
  (:require config))


; all dyn4j coords clients get via the api get scaled up by this factor
(def scale-factor 100)

(defonce ^World world (World.))


; 1 simulation step
(defn up
  []
  (.update world 1)
  (.step world config/world-steps))


(defn create-box
  ; TODO make a struct for x y color ang-vel and lin-vel, because it will be used for other types too, extract method for setting these then
  [w h x y color & [mass-type]]

  (let [rectBody (Body.)
        rectShape (Rectangle. w h)
        mass-type (or mass-type Mass$Type/NORMAL)]
    (.addFixture rectBody rectShape)
    (.setUserData rectBody {:width w :height h :color color})
    (.setMass rectBody mass-type)
    (.translate rectBody x y)
    rectBody)
  )


(defn add-bodies
  [bodies]
  (doseq [body bodies]
    (.addBody world body)))


(defn remove-bodies
  []
  (.removeAllBodies world))


; can be used for painting all the bodies
(defn get-bodies
  []
  (if (not (.getBodies world)) []
    (map
      (fn prepare-body
           [^Body body]
           {:x      (* scale-factor (.x (.getTranslation (.getTransform body))))
            :y      (* scale-factor (.y (.getTranslation (.getTransform body))))
            :width  (* scale-factor (:width (.getUserData body)))
            :height (* scale-factor (:height (.getUserData body)))
            :color  (:color (.getUserData body))
            :body   body
            })
      (.getBodies world))))


