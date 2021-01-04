(ns ^:figwheel-no-load field-proto-reagent.dev
  (:require
    [field-proto-reagent.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
