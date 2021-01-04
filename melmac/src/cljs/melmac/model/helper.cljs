(ns melmac.model.helper)

(defn- nested-reduce-kv [target updates]
  (reduce-kv
   (fn [m k v]
     (reduce-kv (fn [m1 k1 v1] (assoc-in m1 [k k1] v1)) m v))
   target
   updates))

(defn update- [text text-changeset keyword]
  (let [text-new-props          (reduce-kv assoc text text-changeset)
        updated-citations       (nested-reduce-kv (keyword text) (keyword text-changeset))]

    (assoc text-new-props keyword updated-citations)))

(defn delete-item [text id keyword:items keyword:selected-item]
  (-> text
      (update keyword:items dissoc id)
      (assoc keyword:selected-item nil)))

(defn create-item [reading new-item keyword:items]
  (let [keys         (keys (keyword:items reading))
        new-key      (inc (apply max (if (not= nil keys) keys [-1])))
        updated-text (assoc-in reading [keyword:items new-key] new-item)]
    [updated-text new-key]))