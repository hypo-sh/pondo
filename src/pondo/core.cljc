(ns pondo.core)

(defn -at [to xform]
  (fn [data]
    (let [[path-elem & rest-path-elems] to]
      (if path-elem
        (if (seq rest-path-elems)
          ((-at (second (path-elem data)) rest-path-elems) xform)
          (assoc data path-elem
                 [(get-in data [path-elem 0]) (xform (second (path-elem data)))]))
        (xform data)))))

(defmacro at
  [path & body]
  (let [body (map (fn [fm] (list fm)) body)]
    `(-at ~path
          (fn [state#]
            (-> state#
                ~@body)))))

(defn unmount
  "Remove component at key k"
  [k]
  (fn [state]
    (dissoc state k)))

(defn clear
  "Remove all components"
  []
  (constantly {}))

(defn mount
  "mount component at key k with route-data v"
  [k v]
  (fn [state]
    (assoc state k [v nil])))

(defn change
  "update route data at key k b calling (f route-data)"
  [k f]
  (fn [state]
    (update-in state [k 0] f)))

(defn route-data-at
  [path route-data]
  (let [next (rest path)]
    (if (seq next)
      (recur next (-> route-data ((first path)) second))
      (-> route-data ((first path)) first))))

(comment
  (def state
    {:events [1 {:origin [nil nil]
                 :list [3 nil]
                 :detail [nil {:d [nil nil]}]}]})

  (= (route-data-at [:events :list] state) 3)

;; non-macro form
  (-> state
      ((-at [:events]
            (fn [state]
              (-> state
                  ((unmount :detail))
                  ((mount :recording 1))
                  ((change :list inc))
                  ((-at [:recording]
                        (fn [state]
                          (-> state
                              ((mount :moo nil)))))))))))

  ;; macro form
  (= ((at [:events]
          (unmount :detail)
          (mount :recording 1)
          (change :list inc)
          (at [:recording]
              (mount :moo nil))) state)

     {:events [1 {:origin [nil nil]
                  :list [4 nil]
                  :recording [1 {:moo [nil nil]}]}]}))
