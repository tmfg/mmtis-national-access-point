(defproject napote-dashboard "0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [http-kit "2.2.0"]
                 [compojure "1.6.0"]
                 [cheshire "5.8.0"]
                 [amazonica "0.3.118"]

                 [reagent "0.8.0-alpha2"]
                 [figwheel "0.5.13"]
                 [webjure/tuck "0.4.3"]
                 [cljs-ajax "0.7.2"
                  :exclusions [org.apache.httpcomponents/httpasyncclient]]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.cognitect/transit-cljs "0.8.243"]
                 [com.atlassian.commonmark/commonmark "0.11.0"]]

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [org.clojure/tools.nrepl "0.2.12"]]}}

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.13"]]

  :source-paths ["src/clj"]

  :cljsbuild {:builds
              [{:id "dev"
                 :source-paths ["src/cljs"]
                 :figwheel {:on-jsload "dashboard.main/reload-hook"}
                 :compiler {:optimizations :none
                            :source-map true
                            :output-to "resources/public/js/dashboard.js"
                            :output-dir "resources/public/js/out"}}
               {:id "prod"
                :source-paths ["src/cljs"]
                :compiler {:optimizations :advanced
                           :output-to "resources/public/js/dashboard.js"}}]}

  :clean-targets ^{:protect false}
  ["resources/public/js/dashboard.js" "resources/public/js" "target/classes"]

  :repl-options {:init-ns dashboard.main
                 :init (dashboard.main/start)})
