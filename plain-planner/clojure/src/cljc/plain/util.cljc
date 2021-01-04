(ns plain.util)




(defn move-upward [vector item]

  "Given a vector of items and an item, moves the item,
  if it exists, one position more to the front in the vector"

  (if (nil? item)
    vector
    (let [goal-idx (.indexOf vector item)]
      (if (> goal-idx 0)
        (let [goal-to-move-down (nth vector (dec goal-idx))]
          (-> vector
              (assoc (dec goal-idx) item)
              (assoc goal-idx goal-to-move-down)))
        vector))))



(defn insert-into-items-with-ids [items-with-ids item-with-id]
  ; todo identify goal by its id, write test or find existing test; also this is more general

  "Inserts the given item into the list of items.
  Overwrites an existing goal, identified by its id."

  (let [pred                   #(not= (:id item-with-id) (:id %)) ; todo review if something like on is possible here
        before                 (take-while pred items-with-ids)
        after                  (rest (drop-while pred items-with-ids))
        udpated-items-with-ids (concat before [item-with-id] after)]
    (into [] udpated-items-with-ids)))