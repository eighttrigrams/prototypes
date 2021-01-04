(ns melmac.main
  (:require
    [melmac.components.styles :as styles]
    [melmac.components.pages.reading.page :as reading]
    [melmac.components.pages.writing.page :as writing]
    [melmac.context.readings :as readings]
    [melmac.context.writings :as writings]
    [melmac.components.sidebar.component :as sidebar]
    [reagent.core :as reagent
     :refer           [atom]]
    [clojure.string :as string
     :refer             [split-lines]])
  (:require-macros [melmac.tools :as tools]))

(enable-console-print!)

(tools/reverse-it (nrp "It's Melmac, Yo!"))

;; just to demonstrate that macros work

(defn root-component []
  (let [show-sidebar (reagent/atom false)]
    (fn []
      (let [selected-text-metadata             (reagent/atom nil)
            selected-text-type                 (reagent/atom "writing") ;; nil or "reading" or "writing"
            init-text-and-fetch-info!          (fn [text-id text-type]
                                                 (if (= text-type "reading")
                                                   (do
                                                     (readings/initialize text-id)
                                                     (reset! selected-text-type "reading")
                                                     (reset! selected-text-metadata (readings/get-metadata)))
                                                   (do
                                                     (writings/initialize text-id)
                                                     (reset! selected-text-type "writing")
                                                     (reset! selected-text-metadata (writings/get-metadata)))))
            select-text!                       (fn [text-id text-type]
                                                 (reset! show-sidebar false)
                                                 (init-text-and-fetch-info! text-id text-type))]

        (init-text-and-fetch-info! "demo" "writing")
        (fn []
          [:div
           {:style {:background-color styles/main-background-color}}
           (cond @show-sidebar [sidebar/component show-sidebar select-text! @selected-text-type])
           (if (= @selected-text-type "reading")
             [reading/page show-sidebar selected-text-metadata]
             [writing/page show-sidebar selected-text-metadata])])))))

(reagent/render
 [root-component]
 (js/document.getElementById "app-container"))
