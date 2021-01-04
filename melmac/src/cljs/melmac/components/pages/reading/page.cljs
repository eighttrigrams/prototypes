(ns melmac.components.pages.reading.page
  (:require
    [reagent.core :as reagent]
    [melmac.context.readings :as readings]
    [melmac.components.pages.reading.styles :as styles]
    [melmac.components.pages.reading.sidebar :as sidebar]
    [melmac.components.pages.editor :as editor]
    [melmac.components.pages.blend :as blend]))

(defn- update-reading [[om:citation-editor
                        om:comment-editor]
                       {om:selected-item-id :selected-item-id
                        om:item-title       :item-title}]

  {:selected-citation @om:selected-item-id
   :citations         {@om:selected-item-id {:page     @om:item-title
                                             :citation (editor/get-contents @om:citation-editor)
                                             :comment  (editor/get-contents @om:comment-editor)}}})

(defn- update-current-reading!
  ([editors atoms] (update-current-reading! editors atoms false))
  ([editors
    {om:selected-item-id :selected-item-id
     om:item-title       :item-title
     :as                 atoms}
    skip-deletion]

   (cond (not= nil @om:selected-item-id)
     (do
       (if (and (= "\\d" @om:item-title) (not skip-deletion))
         (readings/delete-citation! @om:selected-item-id)
         (readings/update! (update-reading editors atoms)))))))

(defn- on-select!
  [[citation-editor:atom
    comment-editor:atom]
   {previously-selected-id:atom :selected-item-id
    om:item-title               :item-title}]

  (let [set-editor-contents!   #(editor/set-contents! %1 (readings/get-citation-property %2 %3))]
    (fn [id-to-select]
      (set-editor-contents! @comment-editor:atom id-to-select :comment)
      (set-editor-contents! @citation-editor:atom id-to-select :citation)
      (reset! om:item-title (readings/get-citation-property id-to-select :page))
      (reset! previously-selected-id:atom id-to-select))))

(defn- set-editors! [[citation-editor:atom comment-editor:atom]]
  (reset! citation-editor:atom (editor/quill "citation"))
  (reset! comment-editor:atom (editor/quill "comment")))

(defn- page:render [{om:selected-item-id :selected-item-id :as atoms} api]
  (fn []
    [:div
     {:style (styles/page:render)}
     (cond (= nil @om:selected-item-id) [blend/component])
     [sidebar/component atoms api]
     [editor/component "citation" 0]
     [editor/component "comment" 1]]))

(defn page [om:show-sidebar om:reading-metadata]
  (fn []
    @om:reading-metadata ;; this is crucial, because we want all the atoms to re-initialize on change of text

    (let [editors                 [(atom nil) (atom nil)]
          atoms                   {:text-metadata    om:reading-metadata
                                   :selected-item-id (reagent/atom nil)
                                   :item-title       (reagent/atom "")}
          select!                 (fn [id-to-select]
                                    (update-current-reading! editors atoms (= id-to-select (deref (:selected-item-id atoms))))
                                    ((on-select! editors atoms) id-to-select)
                                    id-to-select)
          add!                    #(select! (readings/create-citation!))
          api                     {:get-all-items readings/all
                                   :select-item!  select!
                                   :add-item!     add!
                                   :show-sidebar! #(do
                                                    (update-current-reading! editors atoms)
                                                    (reset! om:show-sidebar true))}]

      [(reagent/create-class
        {:component-did-mount #(do (set-editors! editors)
                                (cond (not= nil (:selected-citation @om:reading-metadata))
                                  ((on-select! editors atoms) (:selected-citation @om:reading-metadata))))
         :reagent-render      (page:render atoms api)})])))