(defproject field-proto-reagent "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [cljsjs/reactstrap "7.1.0-0"]
                 [ring-server "0.5.0"]
                 [reagent "1.0.0"]
                 [reagent-utils "0.3.3"]
                 [clj-http "3.12.1"]
                 [cljs-ajax "0.8.1"]
                 [garden "1.3.10"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [hiccup "2.0.0-alpha2"]
                 [yogthos/config "1.1.1"]
                 [org.clojure/clojurescript "1.10.758"
                  :scope "provided"]
                 [metosin/reitit "0.5.12"]
                 [pez/clerk "1.0.0"]
                 [venantius/accountant "0.2.4"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.1.0"]
            [lein-cljsbuild "1.1.7"]
            [lein-garden "0.3.0"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler field-proto-reagent.handler/app
         :uberwar-name "field-proto-reagent.war"}

  :min-lein-version "2.5.0"
  :uberjar-name "field-proto-reagent.jar"
  :main field-proto-reagent.server
  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc" "src/garden"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  {:assets
   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
             {:output-to        "target/cljsbuild/public/js/app.js"
              :output-dir       "target/cljsbuild/public/js"
              :source-map       "target/cljsbuild/public/js/app.js.map"
              :optimizations :advanced
              :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel {:on-jsload "field-proto-reagent.core/mount-root"}
             :compiler
             {:main "field-proto-reagent.dev"
              :asset-path "/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}}}

 :garden
  {:builds [{:source-paths ["src/garden"]
             :stylesheet field-proto-reagent.styles/style
             :compiler {:output-to "resources/public/css/garden.css"}}]}


 :figwheel
  {:http-server-root "public"
   :server-port 3449
   :server-logfile false ; <- so we see the output in the repl
   :nrepl-port 7002
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl]
   :css-dirs ["resources/public/css"]
   :ring-handler field-proto-reagent.handler/app}
   :profiles {:dev {:repl-options {:init-ns field-proto-reagent.repl}
                    :dependencies [[cider/piggieback "0.3.10"]
                                   [binaryage/devtools "0.9.10"]
                                   [ring/ring-mock "0.4.0"]
                                   [ring/ring-devel "1.9.1"]
                                   [prone "1.6.1"]
                                   [figwheel-sidecar "0.5.20"]
                                   [nrepl "0.4.5"]
                                   [pjstadig/humane-test-output "0.9.0"]]
                    :source-paths ["env/dev/clj"]
                    :plugins [[lein-figwheel "0.5.20"]]
                    :injections [(require 'pjstadig.humane-test-output)
                                 (pjstadig.humane-test-output/activate!)]

                    :env {:dev true}}
              :uberjar {:hooks [minify-assets.plugin/hooks]
                        :source-paths ["env/prod/clj"]
                        :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                        :env {:production true}
                        :aot :all
                        :omit-source true}})
