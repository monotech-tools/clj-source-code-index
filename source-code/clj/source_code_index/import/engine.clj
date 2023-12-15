
(ns source-code-index.import.engine
    (:require [fruits.vector.api :as vector]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn import-source-file
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (map) file-data
  ;
  ; @return (map)
  [_ _ file-data]
  (letfn [(f0 [%] (vector/->items % :name))]
         (-> file-data (dissoc :ns-map)
                       (assoc  :ns-name                          (-> file-data :ns-map :declaration :name))
                       (update :declarations vector/concat-items (-> file-data :ns-map :defs  f0))
                       (update :declarations vector/concat-items (-> file-data :ns-map :defns f0)))))

(defn import-source-path
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (map) source-data
  ;
  ; @return (map)
  [index options source-data]
  (letfn [(f0 [%] (import-source-file index options %))]
         (update source-data :source-files vector/->items f0)))

(defn import-source-paths
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ;
  ; @return (maps in vector)
  [index options]
  (letfn [(f0 [%] (import-source-path index options %))]
         (vector/->items index f0)))
