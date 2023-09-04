# Pondo

Pondo is an experimental, opinionated cljs router designed for single-page applications. 

Traditionally, server-side routing systems use a notion of a "path" to locate a handler (a node) within a route tree. This abstraction makes sense for HTTP servers, since HTTP handlers tend to be a single function that operates upon a request object. A single path is sufficent to unambiguously point to any handler.

User interfaces are structured differently, and need a different mechanism to effectively describe the current UI state.

A user interface is composed of nested components. Some of these components naturally represent a place where children will be either mounted or not mounted depending on the intentions of a user: think tabs in a tab view, for example. In pondo, we call these decision points _mount points_.

Each mount point is named by a keyword, and may optionally be associated with a piece of state (called _params_). For example, in a master-detail view, the state may represent the ID of the currently-selected item.

```clojure
(e/defn User [_path params]
  (dom/div
    (dom/text "Name: " (:name (db/get-by-ref (:ref params))))))

(e/defn Users [_path _params]
  (dom/div
    (Mount. :list List)
    (Mount. :user User)))

(e/defn ApplicationRoot []
  (UrlRoot.
    (Mount. :users Users)
    (Mount. :recordings Recordings)
    (Mount. :settings Settings)))
```


```clojure
{:users [nil {:user [{:ref :jake} nil]
              :list [{:from 25 :count 25} nil]}]}
```
