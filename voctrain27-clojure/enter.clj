(defn new-entry [provided-question provided-answer]
  {:q     provided-question
   :a     provided-answer
   :phase 1
   :date  (java.time.LocalDate/now)})

(defn enter [dictionary]
  (print "\n\n\n\n===============\n(t)rain\n\n")
  (print (str (char 27) "[;H"))
  (print "Enter question ")
  (flush)
  (def provided-question (read-line))
  (if (= "t" provided-question)
    [dictionary false]
    (do (print "Enter answer ")
      (flush)
      (def provided-answer (read-line))
      [(conj dictionary (new-entry provided-question provided-answer)) true])))