
(ns source-code-index.map.engine
    (:require [io.api :as io]
              [fruits.vector.api :as vector]
              [fruits.map.api :as map]
              [time.api :as time]
              [source-code-map.api :as source-code-map]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn map-source-paths
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ;
  ; @return (maps in vector)
  [_ {:keys [filename-pattern source-paths version]}]
  (let [timestamp (time/timestamp-string)]
       (letfn [(f0 [%] {:source-path % :indexed-at timestamp :version (or version :n/a)
                        :source-files (io/search-files % filename-pattern)})
               (f1 [%] (update % :source-files vector/->items f2))
               (f2 [%] {:filepath % :ns-map (source-code-map/read-ns-map %)})]
              (-> source-paths (vector/->items f0)
                               (vector/->items f1)))))
