(ns plain.data
  (:require [plain.model :as model]))

;; the datastore

(defonce projects-dir "resources/projects/")

(def people (atom {}))

(def goals (atom {}))

(def current-id (atom 15))



(defn generate-id []
  (swap! current-id inc)
  @current-id)



(defn- move-goal-upward [goal]
  (let [])) ; todo



(defn- path-in-project-dir [project-name path]
  (str projects-dir (name project-name) "/" path))



(defn read-data-structure [path]
  (with-open [r (java.io.PushbackReader. (clojure.java.io/reader path))]
    (binding [*read-eval* false]
      (read r))))



(defn write-data-structure [path data]
  (with-open [w (clojure.java.io/writer path)]
    (binding [*out* w]
      (pr data))))



(defn- write-to-disk [project-name state file]
  (write-data-structure (path-in-project-dir project-name file) state))



(defn- add-people-watcher [project-name container]
  (add-watch (project-name container) (keyword (str project-name "-people-watcher"))
             (fn [key atom old-state new-state]
               (write-to-disk project-name new-state "people.clj"))))



(defn- add-goals-watcher [project-name container]
  (add-watch (project-name container) (keyword (str project-name "-goals-watcher"))
             (fn [key atom old-state goals]
               (let [[transformed-goals transformed-tasks] (model/make-goals-writable goals)]
                 (write-to-disk project-name transformed-goals "goals.clj")
                 (write-to-disk project-name transformed-tasks "tasks.clj")))))



(defn read-from-disk [project-name]
  (let [peoples-result                  (read-data-structure (path-in-project-dir project-name "people.clj"))
        tasks-result                    (read-data-structure (path-in-project-dir project-name "tasks.clj"))
        goals-result                    (read-data-structure (path-in-project-dir project-name "goals.clj"))
        goals-with-tasks                (model/transform-goals goals-result tasks-result peoples-result)]
    (swap! people assoc (keyword project-name) (atom peoples-result))
    (swap! goals assoc (keyword project-name) (atom goals-with-tasks))
    (add-people-watcher project-name @people)
    (add-goals-watcher project-name @goals)))


(defn get-project-names []
  (->> (clojure.java.io/file projects-dir)
       file-seq
       (filter #(.isDirectory %))
       (map #(.toPath %))
       (map #(.getFileName %))
       (map str)
       (filter #(not= "templates" %))
       (filter #(not= "projects" %))
       (map keyword)))


(defn- read-files []
  (doseq [project (get-project-names)] (read-from-disk project)))



(read-files)
