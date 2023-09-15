(ns pondo.electric
  (:require #?(:cljs [goog.string :as gstring])
            [pondo.core :as core]
            [clojure.edn :as edn]
            [contrib.electric-goog-history :as hist]
            [hyperfiddle.electric :as e]))

(e/def root-route-data {})
(e/def active-route-data {})
(e/def active-path [])
(e/def config {})

(e/defn UrlRoot
  [conf F & args]
  (e/client
   (let [rd (-> hist/path
                (subs (count (::root-path conf)))
                (gstring/urlDecode)
                (edn/read-string))]
     (binding [config conf
               active-route-data rd
               root-route-data rd]
       (e/apply F args)))))

(e/defn Mount
  [match F]
  (when-let [[params new-active-route-data] (match active-route-data)]
    (binding [active-route-data new-active-route-data
              active-path (conj active-path match)]
      (new F active-path params))))

(e/defn Href [f]
  (let [d (f root-route-data)
        root-path (::root-path config)
        encoded
        (gstring/urlEncode (str d))]
    (str root-path encoded)))

(e/defn RouteData [path]
  (core/route-data-at path root-route-data))
