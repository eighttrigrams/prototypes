(defproject rltb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.dyn4j/dyn4j "3.1.5"]]
  :main rltb.core
  :aot [rltb.core]
  :source-paths ["src" "config"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
