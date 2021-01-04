(ns melmac.data-test
  (:require
    [cljs.test :refer-macros [deftest testing is]]
    [ui.data :as data]))

(deftest fake-test
  (testing "fake description"
           (is (= (:title data/empty-text) "no title"))))