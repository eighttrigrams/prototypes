(defproject plain "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.9.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.async  "0.4.500"]
                 [reagent "0.8.1"]
                 [secretary "1.2.3"]
                 [cljs-http "0.1.46"]
                 [com.fasterxml.jackson.core/jackson-core "2.9.9"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [cheshire "4.0.3"]
                 [ring/ring-json "0.4.0"]
                 [ring-basic-authentication "1.0.5"]
                 [ring-basic-auth-test "0.1.0"]]

  :plugins [[lein-figwheel "0.5.19"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs" "src/clj" "src/cljc"] ; todo do we need src/clj here?
                :figwheel {:on-jsload "plain.core/on-js-reload"}
                :compiler {:main plain.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/plain.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           ;; To console.log CLJS data-structures make sure you enable devtools in Chrome
                           ;; https://github.com/binaryage/cljs-devtools
                           :preloads [devtools.preload]}}
               {:id "min"
                :source-paths ["src/cljs" "src/clj" "src/cljc"]
                :compiler {:output-to "resources/public/js/compiled/plain.js"
                           :main plain.core/main
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"
             :css-dirs ["resources/public/css"] ;; watch and update CSS
             :ring-handler plain.controller/app
             :server-logfile false}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [figwheel-sidecar "0.5.19"]]
                   :source-paths ["src/clj" "src/cljs" "dev"]
                   ;; need to add the compliled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     :target-path]}})
