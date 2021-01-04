(ns melmac.components.styles)

(def main-background-color "#F5F5F5")
(def widget-background-color "#F8F8F8")
(def inverted-background-color "black")

(def mdi-standard-size "18px")

(defn mdi
  ([what] (mdi what mdi-standard-size))
  ([what size] ["mdi" (str "mdi-" what) (str "mdi-" size)]))

(def menu-bar-1-height "24px") ; should be a mdi size: 18|24|36|48

;; z-indices overview
;; editors: 1000
;; editors-blend: 1001
;; sidebar-list-item: 1000
;; sidebar-list-item-input: 1000
