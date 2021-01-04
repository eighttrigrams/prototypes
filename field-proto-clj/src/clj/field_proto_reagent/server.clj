(ns field-proto-reagent.server
    (:require [field-proto-reagent.handler :refer [app]]
              [config.core :refer [env]]
              [ring.adapter.jetty :refer [run-jetty]])
    (:gen-class))

(defn -main [& args]
  (let [port 3001]
    (prn "hilo")
    (run-jetty app {:port port :join? false})))
