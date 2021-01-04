(ns field-proto-reagent.prod
  (:require [field-proto-reagent.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
