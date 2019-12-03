(defproject ote "1.4-SNAPSHOT"
  :dependencies [;; Clojure - Eclipse Public License 1.0
                 [org.clojure/clojure "1.10.1"]
                 ;; ClojureScript (clojure to js) - Eclipse Public License 1.0
                 [org.clojure/clojurescript "1.10.597"]
                 ;; CSV parser for backend
                 [org.clojure/data.csv "0.1.4"]
                 ;; CSV parser for frontend - Eclipse Public License 1.0
                 [testdouble/clojurescript.csv "0.3.0"]

                 ;; Components - MIT
                 [com.stuartsierra/component "0.3.2"]

                 ;; Logging (clj + cljs) - Eclipse Public License 1.0
                 [com.taoensso/timbre "4.10.0"]
                 ;; SLF4J binding for Clojure's Timbre logging library - Eclipse Public License 1.0
                 [com.fzakaria/slf4j-timbre "0.3.8"]

                 ;; PostgreSQL JDBC driver, connection pool and other libraries - BSD-2-Clause License
                 [org.postgresql/postgresql "42.1.4"]
                 ;; Postgis jdbc driver - LGPL 2.1
                 [net.postgis/postgis-jdbc "2.1.7.2"
                  :exclusions [ch.qos.logback/logback-classic
                               ch.qos.logback/logback-core]]
                 ;; HicariCP jdbc driver - Apache 2.0
                 [com.zaxxer/HikariCP "3.4.1"]
                 ;; A low-level Clojure wrapper for JDBC-based access to databases. - Eclipse Public License 1.0
                 [org.clojure/java.jdbc "0.7.1"]
                 ;; A Clojure library for using SQL - Eclipse Public License 1.0
                 [webjure/jeesql "0.4.7"]
                 ;; A library for simple PostgreSQL queries with namespaced keys. - MIT
                 [specql "20190301"]

                 ;; http-kit HTTP server (and client) - Apache License Version 2.0.
                 [http-kit "2.3.0"]
                 ;; Ring middleware for gzip compression. - MIT
                 [bk/ring-gzip "0.2.1"]
                 ;; Ring middleware that prevents CSRF attacks - MIT
                 [ring/ring-anti-forgery "1.3.0"]
                 ;; HTTP library wrapping the Apache HttpComponents client - MIT
                 [clj-http "3.10.0"]
                 ;; Support for multipart file upload - Apache 2.0
                 [commons-fileupload/commons-fileupload "1.3.3"]
                 ;; CDDL GPL 2.0
                 [javax.servlet/javax.servlet-api "4.0.1"]

                 ;; Email - MIT
                 [com.draines/postal "2.0.3"]

                 ;; Routing library for publishing services - Eclipse Public License 1.0
                 [compojure "1.6.1"]

                 ;; Password hashing - Apache 2.0 License
                 [buddy/buddy-hashers "1.4.0"]
                 ;; File hash - Eclipse Public License 1.0
                 [digest "1.4.9"]

                 ;; Cache library - Eclipse Public License 1.0
                 [org.clojure/core.cache "0.8.2"]

                 ;; Transit data format from/to Clojure - Apache License, Version 2.0
                 [com.cognitect/transit-clj "0.8.319"]
                 ;; Transit data format from/to ClojureScript - Apache License, Version 2.0
                 [com.cognitect/transit-cljs "0.8.256"]

                 ;; Lightweight scheduler - Eclipse Public License 1.0
                 [jarohen/chime "0.2.2"]

                 ;; Simple Ajax client for ClojureScript and Clojure - Eclipse Public License 1.0
                 [cljs-ajax "0.8.0"
                  :exclusions [org.apache.httpcomponents/httpasyncclient]]

                 ;; Frontend UI libraries
                 ;; Reagent - MIT
                 [reagent "0.8.0-alpha2" :exclusions [cljs/react cljsjs/react-dom]]
                 ;; Micro framework for building Reagent apps
                 [webjure/tuck "20181204"]
                 ;; CLJSJS React - MIT
                 [cljsjs/react "15.6.1-2"]
                 ;; A Javascript library for building user interfaces - MIT
                 [cljsjs/react-dom "15.6.1-2"]
                 ;; Simple JavaScript charting - MIT
                 [cljsjs/chartjs "2.7.3-0"]
                 ;; Iterop library for material-ui.com - Eclipse Public License 1.0
                 [cljs-react-material-ui "0.2.48"]
                 ;; A chip input field using Material-UI - GPLv3
                 [cljsjs/material-ui-chip-input "0.17.2-0"]
                 ;; JavaScript Library for Mobile-Friendly Interactive Maps - MIT
                 [cljsjs/react-leaflet "1.6.5-0" :exclusions [cljsjs/leaflet]]
                 ;; JavaScript Library for Mobile-Friendly Interactive Maps - MIT
                 [cljsjs/leaflet "1.2.0-0"]
                 ;; URL router - BSD (2-Clause)
                 [funcool/bide "1.6.0"]
                 ;; ClojureScript library for styling UI components - MIT
                 [stylefy "1.14.0"]
                 ;; Support for drawing and editing vectors and markers on Leaflet maps - MIT
                 [cljsjs/leaflet-draw "0.4.12-0"]
                 ;; Slim progress bars for Ajax'y applications. - MIT
                 [cljsjs/nprogress "0.2.0-1"]
                 ;; A markdown parser and compiler - MIT
                 [cljsjs/marked "0.3.5-1"]
                 ;; Visualize your data in your Reagent apps as a tree structure. - MIT
                 [data-frisk-reagent "0.4.5"]

                 ;; Time - Eclipse Public License 1.0
                 [com.andrewmcveigh/cljs-time "0.5.0"]

                 ;; HTML/XML generation from Clojure data - Eclipse Public License 1.0
                 [hiccup "1.0.5"]
                 ;; XML zippers - Eclipse Public License 1.0
                 [org.clojure/data.zip "0.1.3"]

                 ;; GeoTools - LGPL
                 [org.geotools/gt-epsg-wkt "20.0"]
                 [org.geotools/gt-geometry "20.0"]

                 ;; Data/file formats and file handling
                 ;; JSON - MIT
                 [cheshire "5.8.0"]
                 ;; CSV - Eclipse Public License 1.0
                 [org.clojure/data.csv "0.1.4"]
                 ;; ClojureScript wrapper around JSZip. - MIT
                 [cljsjs/jszip "3.1.3-0"]
                 ;; An HTML5 saveAs() FileSaver - MIT
                 [cljsjs/filesaverjs "1.3.3-0"]

                 ;; Amazon Web Services - Eclipse Public License 1.0
                 [amazonica "0.3.121"
                  :exclusions [com.amazonaws/aws-java-sdk
                               com.amazonaws/amazon-kinesis-client]]
                 [com.amazonaws/aws-java-sdk-core "1.11.312"]
                 [com.amazonaws/aws-java-sdk-s3 "1.11.312"]

                 ;; Override old guava version from deps - Apache 2.0
                 [com.google.guava/guava "21.0"]
                 ;; Infer clojure specs from sample data. - Eclipse Public License 1.0
                 #_[spec-provider "0.4.14"]
                 ;; Also in use:
                 ;; Chouette command line tool - CeCILL-B
                 ;; Firejail - GNU General Public License, version 2
                 ]
  :profiles {:uberjar {:aot :all

                       ;; Prevent uberjar from cleaning cljs generated files
                       :auto-clean false}
             :dev {:dependencies [[org.clojure/test.check "0.10.0-alpha2"]
                                  [webjure/json-schema "0.7.4"]]
                   :test-paths ["test/clj"]}}

  :repositories [["osgeo" "https://download.osgeo.org/webdav/geotools/"]
                 ["boundlessgeo" "https://repo.boundlessgeo.com/main/"]]
  :plugins [;; Automatically compile your ClojureScript code into Javascript - Eclipse Public License 1.0
            [lein-cljsbuild "1.1.7"]
            ;; Figwheel builds your ClojureScript code and hot loads it into the browser - Eclipse Public License 1.0
            [lein-figwheel "0.5.13"]
            ;; Static code analyzer for Clojure, ClojureScript - Eclipse Public License 1.0
            [lein-kibit "0.1.6"]]

  ;; Backend sources: clj and cljc which is used in front end (cljc) also
  :source-paths ["src/clj" "src/cljc"]

  ;; ClojureScript build
  :cljsbuild {:builds
              [;; Local development build
               {:id "dev"
                :source-paths ["src/cljs" "src/cljc"]
                :figwheel {:on-jsload "ote.main/reload-hook"}
                :compiler {:optimizations :none
                           ;; :verbose true
                           :source-map true
                           :output-to "resources/public/js/ote.js"
                           :output-dir "resources/public/js/out"}}

               ;; Production build advanced compilation
               {:id "prod"
                :source-paths ["src/cljs" "src/cljc"]
                :compiler {;;:pseudo-names true ; enable for debugging externs
                           :optimizations :advanced
                           :output-to "resources/public/js/ote.js"
                           :output-dir "resources/public/js/prod-out"
                           :source-map "resources/public/js/ote.js.map"
                           :closure-output-charset "US-ASCII"
                           :infer-externs true}}]}

  :clean-targets ^{:protect false}
  ["resources/public/js/ote.js" "resources/public/js" "target/classes"]

  :aliases {;; Alias for doing a full production build
            "production" ["do" "clean," "deps," "compile,"
                          "cljsbuild" "once" "prod,"
                          "postbuild,"
                          "uberjar"]

            "postbuild" ["run" "-m" "ote.tools.postbuild"]
            }
  :repl-options {:init-ns ote.main
                 :init (ote.main/start)
                 :host "localhost"}
  :main ote.main
  :figwheel {:server-ip "localhost"
             :nrepl-host "localhost"
             :server-logfile "/tmp/logs/figwheel-logfile.log"})
