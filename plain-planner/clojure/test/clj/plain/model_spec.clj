(ns plain.model-spec
  (:use clojure.test)
  (:require [plain.model :as model]))



(deftest make-goals-writable-test
  (let [goals [{:title "G1" :id 1 :tasks [{:title "T1" :id 2 :assignee {:name "N" :id 3} }
                                          {:title "T2" :id 4 :assignee {:name "N" :id 3} }]}
               {:title "G2" :id 5 :tasks [{:title "T3" :id 6 :assignee {:name "N" :id 3} }]}]
        [transformed-goals transformed-tasks] (model/make-goals-writable goals)]
    (is (= transformed-goals [{:title "G1", :id 1, :tasks [2 4]} {:title "G2", :id 5, :tasks [6]}]))
    (is (= transformed-tasks [{:title "T1", :id 2, :assignee 3}
                              {:title "T2", :id 4, :assignee 3}
                              {:title "T3", :id 6, :assignee 3}]))))