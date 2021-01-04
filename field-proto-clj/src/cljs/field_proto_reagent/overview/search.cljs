(ns field-proto-reagent.overview.search
  (:require
    [reagent.core :as rg]
    [field-proto-reagent.docs :as docs]
    [ajax.core :refer [GET]]))


(defonce q (rg/atom ""))


(defn is-project-doc [document]
  (= "Project" (-> document :resource :type)))


(defn search-match [search-string]
  (fn [document]
    (clojure.string/starts-with?
        (clojure.string/lower-case (-> document :resource :identifier))
        (clojure.string/lower-case search-string))))


(defn get-matches [documents search-string]
  (->> documents
    (filter is-project-doc)
    (filter (search-match search-string))))


(defn handle-incoming-documents [search-string]
  (fn [documents]
    (swap! q (fn [_] search-string))
    (docs/update-documents! (get-matches (:documents documents) search-string))))


(defn renew-search
  ([] (renew-search @q))
  ([search-string]
    (GET "/documents" {
      :response-format :json
      :keywords? true
      :handler (handle-incoming-documents search-string)})))
