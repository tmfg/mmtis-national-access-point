(ns ote.localization
  "Message localization for both backend and frontend.
  The interface to translate messages is the `tr` function which takes a message
  and possible parameters.

  On the frontend a global `selected-language` atom is used.
  On the backend the language can be dynamically bound with `*language*`."
  (:require #?@(:cljs [[reagent.core :as r]
                       [ote.communication :as comm]
                       [cljsjs.marked]]
                :clj  [[clojure.java.io :as io]])
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [ote.transit :as transit]))

(def supported-languages #{"fi" "sv" "en"})

(defonce loaded-languages (atom {}))

(defn load-language!
  "Load the given language translation file, if it has not been loaded yet, and adds the language
  to the `loaded-languages` atom.
  Calls `on-load` callback, when loading is done."
  [language on-load]
  (if-let [translations (get @loaded-languages language)]
    (on-load language translations)
    #?(:clj (let [translations (-> (str "public/language/" (name language) ".edn")
                                   io/resource slurp read-string)]
              (swap! loaded-languages assoc language translations)
              (on-load language translations))
       :cljs (comm/get! (str "language/" (name language))
                        {:on-success (fn [translations]
                                       (swap! loaded-languages assoc language translations)
                                       (on-load language translations))}))))
(defn translations
  "(Re)loads the given language translation file and returns the translations."
  [language]
  (swap! loaded-languages dissoc language)
  (load-language! language (constantly nil))
  (get @loaded-languages language))

#?(:cljs
   (do
     ;; FIXME: read language from local storage or read from CKAN cookie
     (defonce selected-language (r/atom :fi))
     (defn set-language! [language]
       (load-language! language #(reset! selected-language %1))))

   :clj
   (do
     (def ^:dynamic *language* nil)
     (defmacro with-language
       "Run `body` with `*language*` bound to the given language."
       [language & body]
       `(load-language! ~language
                        (fn [language# _#]
                          (binding [*language* language#]
                            ~@body))))))

(declare message)
(defmulti evaluate-list (fn [[operator & args] parameters] operator))

(defmethod evaluate-list :plural [[_ param-name zero one many] parameters]
  (let [count (get parameters param-name)]
    (cond
      (nil? count) (str "{{missing count parameter " param-name "}}")
      (zero? count) (message zero parameters)
      (= 1 count) (message one parameters)
      :else (message many parameters))))

(defmethod evaluate-list :md [[operator & args] parameters]
  #?(:cljs
     ;; Not really so dangerous, because we are sanitizing the markdown. But, still we'll have to be careful when
     ;; serving the edn files to prevent any possible injection vectors.
     ;; NOTE: We make sure here, that the result of the parsing is stored in a node, so it can be safely
     ;; used anywhere in a ui component.
     [:span
      {:class "translation-markdown"                        ;; Defined in styles.css
       :dangerouslySetInnerHTML
       ;; We are converting args list to vector to trigger message default behaviour (=reduce)
       {:__html (js/marked (message (vec args) parameters) #js {:sanitize true})}}]

     :clj
     ;; Markdown is only supported in ClojureScript
     (throw (ex-info "Markdown formatted translations not supported." {:operator operator :args args}))))

(defmethod evaluate-list :default [[op & _] _]
  (str "{{unknown translation operation " op "}}"))

(defn- message-part [part parameters]
  (cond
    (keyword? part)
    (message-part (get parameters part) parameters)

    (list? part)
    (evaluate-list part parameters)

    :default
    (str part)))

(defn- message [message-definition parameters]
  (cond
    (string? message-definition)
    message-definition

    (list? message-definition)
    (evaluate-list message-definition parameters)

    :default
    (reduce (fn [acc part]
              (str acc (message-part part parameters)))
            ""
            message-definition)))

(defn tr
  "Returns translation for the given message.
  If `language` is provided, use that language, otherwise use the default.

  `message-path` is a vector of keywords path to the translation map.

  Optional `parameters` give values to replaceable parts in the message."
  ([message-path]
   (tr #?(:clj *language* :cljs @selected-language)
       message-path {}))
  ([message-path parameters]
   (tr #?(:clj *language* :cljs @selected-language) message-path parameters))
  ([language message-path parameters]
   (let [language (get @loaded-languages language)]
     (assert language (str "Language " language " has not been loaded, cannot lookup translation for " message-path"."))
     (message (get-in language message-path) parameters))))

(s/fdef tr-key
        :args (s/cat :path (s/coll-of keyword?))
        :ret fn?)

(defn tr-key
  "Returns a function that translates a keyword under the given `path`.
  This is useful for creating a formatting function for keyword enumerations.
  Multiple paths may be provided and they are tried in the given order. First
  path that has a translation for the requested key, is used."
  [& paths]
  (fn [key]
    (or
      (some #(let [message (tr (conj % key))]
               #_(log/info "path: " (pr-str (conj % key)) " => " message ", blank? " (str/blank? message))
               (when-not (str/blank? message)
                 message))
            paths)
      "")))

(defn tr-or
  "Utility for returning a default when a translation is not found."
  [tr-result default-value]
  (if (= "" tr-result)
    default-value
    tr-result))

(defn tr-tree
  ([tree-path]
   (tr-tree #?(:clj *language* :cljs @selected-language) tree-path))
  ([language tree-path]
   (get-in (get @loaded-languages language) tree-path)))

#?(:cljs
   (defn load-embedded-translations! []
     (let [elt (.getElementById js/document "ote-translations")
           {:keys [language translations]} (transit/transit->clj (.-innerText elt))]
       (.removeChild (.-parentNode elt) elt)
       (swap! loaded-languages assoc language translations)
       (reset! selected-language language))))
