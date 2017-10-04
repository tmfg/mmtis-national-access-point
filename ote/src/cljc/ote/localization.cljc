(ns ote.localization
  "Message localization for both backend and frontend.
  The interface to translate messages is the `tr` function which takes a message
  and possible parameters.

  On the frontend a global `selected-language` atom is used.
  On the backend the language can be dynamically bound with `*language*`."
  (:require #?@(:cljs [[reagent.core :as r]
                       [ote.communication :as comm]]
                :clj [[clojure.java.io :as io]])
            [clojure.spec.alpha :as s]))

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
       (load-language! language
                       (fn [language _]
                         (binding [*language* language]
                           ~@body))))))

(defn- message-part [part parameters]
  (cond
    (keyword? part)
    (message-part (get parameters part) parameters)

    ;; PENDING: tässä voitaisiin tehdä Date formatointi yms tyypin mukaista
    :default
    (str part)))

(defn- message [message-definition parameters]
  (if (string? message-definition)
    message-definition
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
   (tr #?(:clj *language* :cljs @selected-language) message-path {}))
  ([language message-path parameters]
   (let [language (get @loaded-languages language)]
     (assert language (str "Language " language " has not been loaded."))
     (message (get-in language message-path) parameters))))

(s/fdef tr-key
        :args (s/cat :path (s/coll-of keyword?))
        :ret fn?)

(defn tr-key
  "Returns a funktion that translates a keyword under the given `path`.
  This is useful for creating a formatting function for keyword enumerations."
  [path]
  (fn [key]
    (tr (conj path key))))
