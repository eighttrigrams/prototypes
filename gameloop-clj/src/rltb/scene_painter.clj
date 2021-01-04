(ns rltb.scene-painter
  (:require
    rltb.scene
    config)
  (:import (java.awt Graphics2D GraphicsEnvironment)
           (java.awt.geom AffineTransform)
           (org.dyn4j.dynamics Body)))


(def ^java.awt.image.BufferedImage img (atom nil))

(def ^java.awt.image.BufferStrategy buffer (atom nil))


(def scale-factor 0.27)


(defn init
  []
  (let [ge (GraphicsEnvironment/getLocalGraphicsEnvironment)
        gc (.getDefaultScreenDevice ge)
        gd (.getDefaultConfiguration gc)
        bi (.createCompatibleImage
             gd
             (:width config/window-size)
             (:height config/window-size))]
    (swap! rltb.scene-painter/img (fn [_] bi))))


(defn paint-landscape
  [^Graphics2D g2d]
  (.setColor g2d config/background-color)
  (.fillRect g2d 0 0
             (- (:width config/window-size) 1)
             (- (:height config/window-size) 1)))


(defn paint-objects
  [^Graphics2D g2d objects]
  (doseq
    [object objects]

    (.setColor g2d (:color object))

    (let [ot (.getTransform g2d)
          at (AffineTransform.)
          rotation (.getRotation (.getTransform ^Body (:body object)))]

      (.translate at (:x object) (- (:y object)))
      (.rotate at rotation)
      (.transform g2d at)

      (.fillRect g2d
                 (- (/ (:width object) 2))
                 (- (/ (:height object) 2))
                 (:width object)
                 (:height object))

      (.setTransform g2d ot))
    ))


(defn paint-it [objects]

    (let [graphics  (.getDrawGraphics @buffer)
          g2d (.createGraphics @img)]

      (try
        (paint-landscape g2d)

        (.scale g2d scale-factor scale-factor)
        (paint-objects g2d objects)

        (.drawImage graphics @img 0, 0, nil)
        (if (not (.contentsLost @buffer)) (.show @buffer))

        (finally
          (if graphics (.dispose graphics))
          (if g2d (.dispose g2d))))))