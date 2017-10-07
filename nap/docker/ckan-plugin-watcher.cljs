#!/usr/bin/env lumo

(def local-path "ckan/ckan-plugins/ckanext-napote_theme")
(def ckan-path "/usr/lib/ckan/src/ckanext-napote_theme")

(defn copy-to-docker [file]
  (let [cmd (str "docker cp " local-path "/" file " napote-ckan:" ckan-path "/" file)]
    (.log js/console "EXEC: " cmd)
    (js/child_process.execSync cmd)))

(defn watcher [event file]
  (when (and (= "change" event)
             (not (re-matches #"^#.*" file)))
    (copy-to-docker file)))

(js/fs.watch local-path
             #js {:recursive true}
             watcher)

