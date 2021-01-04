(ns plain.controller
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:require
    [compojure.handler :as compojure-handler]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
    [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
    [plain.handler :as handler]
    [plain.data :as data]
    [plain.timeline :as timeline]))


(defroutes protected-routes
  (GET "/people/:project" {{:keys [project]} :params} (handler/get-people (keyword project)))
  (POST "/people/:project" {{:keys [project]} :params} (handler/make-person (keyword project)))
  (PUT "/people/:project" {{:keys [project]} :params person :body} (handler/update-person (keyword project) person))

  (GET "/goals/:project" {{:keys [project]} :params} (handler/get-goals (keyword project)))
  (POST "/goals/:project" {{:keys [project]} :params} (handler/make-goal (keyword project)))
  (PUT "/goals/:project" {{:keys [project]} :params goal :body} (handler/put-goal (keyword project) goal))

  (DELETE "/goals/:project/:id" {{:keys [project id]} :params} (handler/delete-goal (keyword project) id))
  (POST "/goals/:project/up/:id" {{:keys [project id]} :params} (handler/move-goal-upward (keyword project) id))

  (POST "/tasks/:project/up/:goal-id/:task-id" {{:keys [project goal-id task-id]} :params} (handler/move-task-upward (keyword project) goal-id task-id))
  (POST "/tasks/:project/:goal-id" {{:keys [project goal-id]} :params} (handler/make-task (keyword project) goal-id))

  (GET "/projects" request (handler/get-projects))

  (GET "/timeline/:project*"
       {{:keys [project]} :params params :query-params}
       {:status 200 :body
        (do (prn "..." project) (timeline/make-timeline (deref ((keyword project) @data/people)) (deref ((keyword project) @data/goals))
                                (Integer/parseInt (get params "from"))
                                (Integer/parseInt (get params "days"))))})
  (route/resources "/")
  (route/not-found "Not Found"))


(defn authenticated? [name pass]
  (comment (prn "try auth" name ":" pass))
  (if (and (= name "foo")
           (= pass "bar"))
    [name pass]
    false))


(defroutes app-routes
  (context "/documents" []
           (defroutes documents-routes
             (GET  "/" request
                   (prn (:basic-authentication request))
                   {:status 200
                    :body {:status "ok!!!!"}}))))

(def app
  (-> (routes
       app-routes
       (wrap-basic-authentication protected-routes authenticated?))
      (compojure-handler/api)
      (wrap-json-body {:keywords? true})
      (wrap-json-response {:keywords? true})))

