(ns melmac.tools
  (:require [clojure.string :as str]
            [clojure.walk :as walk]))


(defmacro reverse-it
  [form]
  (walk/postwalk
    #(if (symbol? %)
      (symbol (str/reverse (name %)))
      %)
    form))