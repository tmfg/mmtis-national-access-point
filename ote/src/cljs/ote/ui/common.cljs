(ns ote.ui.common
  "Common small UI utilities")

(defn linkify [url label]
  (if-not url
    [:span]
    (let [url (if (re-matches #"^\w+:.*" url)
                url
                (str "http://" url))]
      [:a {:href url :target "_blank"} label])))
