(ns melmac.components.pages.editor
  (:require [melmac.components.pages.styles :as styles]))

(def Quill
  (js/require
   "quill"))

(defn quill [id] (Quill. (str "#quill-" id)))

(defn get-contents [editor]
  (-> editor .getContents .-ops js->clj))

(defn set-contents! [editor contents]
  (.setContents editor (clj->js contents)))

(defn component
  [name position]
  [:div
   {:class "col-md-5 pl-1 pr-1"
    :style (styles/citations-editor position)}
   [:div
    {:id    (str "quill-" name)
     :style (styles/quill-editor-element)}]])