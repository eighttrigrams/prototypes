(ns field-proto-reagent.handler
  (:require [reitit.ring :as reitit-ring]
            [clj-http.client :as client]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [field-proto-reagent.middleware :refer [middleware]]
            [hiccup.page :refer [include-js include-css html5]]
            [config.core :refer [env]]))

(defn index-handler
  [_request]
  (prn "index")
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (slurp "resources/public/index.html")})


 (def documents {:documents [
   {:resource {:type "Project" :id "a" :identifier "Amsterdam Project" :short-description "The Amsterdam Project" :geometry {:lat 52.378 :long 4.9 :zoom 12}}}
   {:resource {:type "Project" :id "b" :identifier "Cologne Project" :short-description "The Cologne Project" :geometry {:lat 50.94222 :long 6.95778 :zoom 12}}}
   {:resource {:type "Project" :id "c" :identifier "London Project" :short-description "The London Project" :geometry {:lat 51.50722 :long -0.1275 :zoom 12}}}
   {:resource {:type "Project" :id "d" :identifier "Uruk Project" :short-description "The Uruk Project" :geometry {:lat 39.190460 :long 37.968910 :zoom 15}}}
   {:resource {:type "Project" :id "e" :identifier "Selinunt Project" :short-description "The Selinunt Project" :geometry {:lat 37.586430 :long 12.838060 :zoom 13}}}
   {:resource {:type "Project" :id "f" :identifier "Meninx Project" :short-description "The Meninx Project" :geometry {:lat 33.832350 :long 10.984190 :zoom 11}}}]})


(defn documents-handler
 [_request]
 (prn (str "hello from the backend's documents-handler"))
 ; (client/get "http://localhost:3001"))
 (response documents))


(def app*
  (reitit-ring/ring-handler
   (reitit-ring/router
    [["/" {:get {:handler index-handler}}]
     ["/documents" {:get {:handler (wrap-json-response documents-handler)}}]]
    {:data {:middleware middleware}})
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))))


(defn app [req]
  (prn req)
  (app* req))
