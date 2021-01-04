(ns rltb.core
  (:gen-class)
  (:import (javax.swing Timer JFrame)
           (java.awt Canvas))
  (:require
    rltb.gameloop
    rltb.scene
    config))


(defn -main
  [& args]
  (let [app (JFrame.)
        timer (Timer. config/fps (rltb.gameloop.))
        canvas (Canvas.)]

      (.setIgnoreRepaint app true )
      (.setDefaultCloseOperation app JFrame/EXIT_ON_CLOSE)
      (.setIgnoreRepaint canvas true )
      (.setSize canvas (:width config/window-size) (:height config/window-size))
      (.add app canvas)
      (.pack app)
      (.setVisible app true)
      (.createBufferStrategy canvas 2)
      (.setRepeats timer true)

      (rltb.scene-painter/init)
      (swap! rltb.scene-painter/buffer (fn [_] (.getBufferStrategy canvas)))
      (.start timer)))




