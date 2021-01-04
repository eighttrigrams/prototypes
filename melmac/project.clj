(defproject melmac "0.1.0-SNAPSHOT"
  :license {:name "Apache 2.0"}
  :source-paths ["src/cljs" "src/cljc" "src/dev"]
  :description "A study of a knowledge management app"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [figwheel "0.5.19"]
                 [figwheel-sidecar "0.5.18"]
                 [cider/piggieback "0.4.0"]
                 [reagent "0.8.1"]
                 [ring/ring-core "1.7.1"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.19"]
            [lein-doo "0.1.6"]]

  :clean-targets ^{:protect false} ["resources/main.js"
                                    "resources/public/js/ui-core.js"
                                    "resources/public/js/ui-core.js.map"
                                    "resources/public/js/ui-out"]
  :cljsbuild
  {:builds
   [{:id "test"
     :source-paths ["src/cljs" "src/cljc" "test/ui" ]
     :compiler {:output-to "resources/public/js/ui-test.js"
                :optimizations :none
                :target :nodejs}}
    {:source-paths ["src/cljs" "src/cljc" "src/dev"]
     :id "dev"
     :compiler {:output-to "resources/public/js/ui-core.js"
                :output-dir "resources/public/js/ui-out"
                :source-map true
                :asset-path "js/ui-out"
                :optimizations :none
                :npm-deps {"quill" "1.3.7"
                           "bootstrap" "4.1.3"
                           "@mdi/font" "4.5.95"
                           "electron" "7.1.1"}
                :install-deps true
                :cache-analysis true
                :main "dev.core"}}]}
  :figwheel {:http-server-root "public"
             :css-dirs ["resources/public/css"]
             :ring-handler middleware.figwheel-middleware/app
             :server-port 3449
             :server-logfile "logs/figwheel_server.log"})
