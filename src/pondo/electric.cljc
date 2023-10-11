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
  (e/client
    ;; Support "default" mount - that is, mount at this depth iff no other
    ;; functions will mount at this depth.
    ;; Particularly useful at the root of the tree to support the case where
    ;; no routes are present.
   (if (nil? match)
     (when
      (empty? active-route-data)
       (new F active-path active-route-data))
     (when-let [[params new-active-route-data] (match active-route-data)]
       (binding [active-route-data new-active-route-data
                 active-path (conj active-path match)]
         (new F active-path params)
         true)))))

(e/defn Href [f]
  (e/client
   (let [d (f root-route-data)
         root-path (::root-path config)
         encoded
         (gstring/urlEncode (str d))]
     (str root-path encoded))))

(e/defn RouteData [path]
  (core/route-data-at path root-route-data))
