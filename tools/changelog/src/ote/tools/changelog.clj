(ns ote.tools.changelog
  "Generate changelog from Github PR logs"
  (:import (org.commonmark.parser Parser)
           (org.commonmark.renderer.html HtmlRenderer))
  (:require [org.httpkit.client :as http]
            [cheshire.core :as cheshire]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def github-pull-request-url "https://api.github.com/repos/finnishtransportagency/mmtis-national-access-point/pulls?state=closed")

;; Parse link header for the next item
(defn parse-link-header [link]
  (when-let [next-link
             (when link
               (some #(when (str/ends-with? % "rel=\"next\"") %)
                     (str/split link #", ")))]
    (second (re-matches #"<(.*)>; rel=\"next\"" next-link))))

(def next-link-example "<https://api.github.com/repositories/96249016/pulls?state=closed&page=2>; rel=\"next\", <https://api.github.com/repositories/96249016/pulls?state=closed&page=9>; rel=\"last\"")

(defn fetch-pull-requests
  ([] (fetch-pull-requests github-pull-request-url))
  ([url]
   (println "Fetching Github API URL: " url)
   (let [response @(http/get url {:as :stream})
         next-link (parse-link-header (get-in response [:headers :link]))
         results (cheshire/decode-stream
                  (io/reader (:body response))
                  (comp keyword #(str/replace % #"_" "-")))]
     (if next-link
       (into results (fetch-pull-requests next-link))
       results))))

(defn merged-pull-requests [prs]
  (filter :merged-at prs))

(defn parse-body [{body :body :as pr}]
  (loop [sections {}
         current-section nil
         [l & lines] (str/split-lines body)]
    (if-not l
      sections
      (if (str/starts-with? l "# ")
        (recur (assoc sections (subs l 2) "")
               (subs l 2)
               lines)
        (if current-section
          (recur (update sections current-section str l "\n")
                 current-section
                 lines)
          (recur sections current-section lines))))))

(comment
  (parse-body {:body "# Added
- foo-page now has sorting options

# Fixed
- bar function no longer throws NPE
- delete button now works

# Changed
- Changed an existing feature.
- Removed unused menu item.

# Environment
- added jenkins job to do something"})
  ;; => {"Added" "- foo-page now has sorting options\n\n",
  ;;     "Fixed" "- bar function no longer throws NPE\n- delete button now works\n\n",
  ;;     "Changed" "- Changed an existing feature.\n- Removed unused menu item.\n\n",
  ;;     "Environment" "- added jenkins job to do something\n"}
)

(defn merge-day [pr]
  (subs (:merged-at pr) 0 10))

(defn format-section [name items]
  (if (str/blank? items)
    ""
    (str "\n## " name "\n"
         items)))

(defn prepare-changelog [prs]
  (let [prs-by-day (reverse (sort-by first
                                     (group-by merge-day
                                               (merged-pull-requests prs))))]

    (str/join
     (for [[day prs] prs-by-day
           :let [all-sections (apply merge-with str (map parse-body prs))]]
       (if (empty? all-sections)
         ""
         (str "\n\n# " day "\n"
              (format-section "Added" (get all-sections "Added"))
              (format-section "Fixed" (get all-sections "Fixed"))
              (format-section "Changed" (get all-sections "Changed"))
              (format-section "Datamodel" (get all-sections "Datamodel"))
              (format-section "Environment" (get all-sections "Environment"))))))))

(defn to-html [markdown-string]
  (let [p (.build (Parser/builder))
        document (.parse p markdown-string)
        renderer (.build (HtmlRenderer/builder))]
    (spit "changelog.html" (.render renderer document))))

(defn -main [& args]
  (to-html (prepare-changelog (fetch-pull-requests))))
