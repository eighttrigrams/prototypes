(clojure.main/load-script "train.clj")
(clojure.main/load-script "enter.clj")
(clojure.main/load-script "fs.clj")

(def file-name "prod.edn")

(loop [[dict data-entry] [(load-dict file-name) true]]
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H"))
  (let [[dict data-entry] (if data-entry (enter dict) (train dict))]
    (write-dict dict file-name)
    (recur [dict data-entry])))