;; Here we have the dependency to the file system.
;; And we determine the file encoding and ending.
;; We catch errs and print them to the console.
;; We print to the console when writing to disk

(ns melmac.datastore.filesystem
  (:require [cljs.reader :as reader]))

(defonce fs
  (js/require
   "fs"))

(defn write-to-file
  [path content]
  (try
    (.writeFileSync fs (str path ".edn") content "utf-8")
    (catch js/Object e (.log js/console "caught error while writing file" e))))

(defn- read-from-file
  [path]
  (try
    (reader/read-string
     (.readFileSync fs (str path ".edn") "utf-8"))
    (catch js/Object e (.log js/console "caught error while reading file" e))))

(defn load-safely [path is-valid? on-success]
  (cond (.existsSync fs (str path ".edn"))
    (let [loaded-text (read-from-file path)]
      (if (is-valid? loaded-text)
        (on-success loaded-text)
        (js/alert "Something went wrong!")))))

(defn update-safely! [updated-text path on-success]
  (prn "Writing to disk ...")
  (write-to-file path updated-text)
  (on-success))

(defn get-available-texts-ids [path]
  (->> (.readdirSync fs path)
       (filter #(clojure.string/ends-with? % ".edn"))
       (map #(clojure.string/replace % #".edn" ""))))