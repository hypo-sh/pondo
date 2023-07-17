(ns pondo.electric
  (:require [goog.string :as gstring]
            [clojure.edn :as edn]
            [contrib.electric-goog-history :as hist]
            [hyperfiddle.electric :as e]))

(e/def active-route-data {})
(e/def active-path [])
(e/def config {})

(e/defn UrlRoot
  [conf F]
  (e/client
   (println (-> hist/path (subs 4)))
   (binding [config conf
             active-route-data
             (-> hist/path
                 (subs (count (::root-path conf)))
                 (gstring/urlDecode)
                 (edn/read-string))]
     (new F))))

(e/defn Mount
  [match F]
  (when-let [[params new-active-route-data] (match active-route-data)]
    (binding [active-route-data new-active-route-data
              active-path (conj active-path match)]
      (new F active-path params))))

(e/defn Href [f]
  (let [d (f active-route-data)
        root-path (::root-path config)
        encoded
        (gstring/urlEncode (str d))]
    (str root-path encoded)))
