(ns melmac.components.pages.writing.page
  (:require
    [melmac.context.writings :as writings]
    [melmac.components.pages.writing.styles :as styles]
    [melmac.components.pages.writing.sidebar :as sidebar]
    [reagent.core :as reagent]
    [melmac.components.pages.editor :as editor]
    [melmac.components.pages.blend :as blend]))

(defn- update-writing [om:editor
                       {om:selected-item-id :selected-item-id
                        om:item-title       :item-title}]

  {:selected-section @om:selected-item-id
   :sections         {@om:selected-item-id {:title   @om:item-title
                                            :content (editor/get-contents @om:editor)}}})

(defn- update-current-writing!
  ([editor atoms] (update-current-writing! editor atoms false))
  ([editor
    {om:selected-item-id :selected-item-id
     om:item-title       :item-title
     :as                 atoms}
    skip-deletion]

   (cond (not= nil @om:selected-item-id)
     (do
       (if (and (= "\\d" @om:item-title) (not skip-deletion))
         (writings/delete-section! @om:selected-item-id)
         (writings/update! (update-writing editor atoms)))))))

(defn- on-select!
  [om:editor
   {previously-selected-id:atom :selected-item-id
    om:item-title               :item-title}]
  (fn [id-to-select]
    (editor/set-contents! @om:editor (writings/get-sections-property id-to-select :content))
    (reset! om:item-title (writings/get-sections-property id-to-select :title))
    (reset! previously-selected-id:atom id-to-select)))

(defn- page:render [{om:selected-item-id :selected-item-id :as atoms} api]
  (fn []
    [:div
     {:style styles/page:render}
     (cond (= nil @om:selected-item-id) [blend/component])
     [sidebar/component atoms api]
     [editor/component "writing" 0]
     [:div {:style styles/blend}]]))

(defn page [om:show-sidebar om:writing-metadata]
  (fn []
    @om:writing-metadata ;; this is crucial, because we want all the atoms to re-initialize on change of text

    (let [editor                  (atom nil)
          atoms                   {:text-metadata    om:writing-metadata
                                   :selected-item-id (reagent/atom nil)
                                   :item-title       (reagent/atom "")}
          select!                 (fn [id-to-select]
                                    (update-current-writing! editor atoms (= id-to-select (deref (:selected-item-id atoms))))
                                    ((on-select! editor atoms) id-to-select)
                                    id-to-select)
          add!                    #(select! (writings/create-section!))
          api                     {:get-all-items writings/all
                                   :select-item!  select!
                                   :add-item!     add!
                                   :show-sidebar! #(do
                                                    (update-current-writing! editor atoms)
                                                    (reset! om:show-sidebar true))}]

      [(reagent/create-class
        {:component-did-mount #(do (reset! editor (editor/quill "writing"))
                                (cond (not= nil (:selected-section @om:writing-metadata))
                                  ((on-select! editor atoms) (:selected-section @om:writing-metadata))))
         :reagent-render      (page:render atoms api)})])))