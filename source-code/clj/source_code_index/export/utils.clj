
(ns source-code-index.export.utils
    (:require [fruits.vector.api               :as vector]
              [source-code-index.core.messages :as core.messages]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn source-path-indexed?
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (string) source-path
  ;
  ; @return (boolean)
  [index source-path]
  (letfn [(f0 [%] (-> % :source-path (= source-path)))]
         (vector/any-item-matches? index f0)))

(defn source-path-not-indexed?
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (string) source-path
  ;
  ; @return (boolean)
  [index source-path]
  (-> (source-path-indexed? index source-path) not))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn declaration-indexed?
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (string) filepath
  ; @param (string) name
  ;
  ; @return (boolean)
  [index filepath name]
  (letfn [(f0 [%] (-> %               (vector/last-result f1))) ; <- The last source path in the given index that contains the given filepath.
          (f1 [%] (-> % :source-files (vector/first-match f2))) ; <- ...
          (f2 [%] (-> % :filepath     (= filepath)))            ; <- ...
          (f3 [%] (-> % :declarations (vector/contains-item? name)))]
         (-> index f0 f3)))

(defn declaration-not-indexed?
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (string) filepath
  ; @param (string) name
  ;
  ; @return (boolean)
  [index filepath name]
  (-> (declaration-indexed? index filepath name) not))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn any-declaration-changed?
  ; @ignore
  ;
  ; @param (map) changes
  ;
  ; @return (boolean)
  [changes]
  (or (-> changes :added   vector/not-empty?)
      (-> changes :removed vector/not-empty?)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn filter-stored-index
  ; @ignore
  ;
  ; @description
  ; Removes source paths from the given 'stored-index' that are not provided in the 'source-paths' property.
  ;
  ; @param (maps in vector) stored-index
  ; @param (maps in vector) updated-index
  ; @param (map) options
  ;
  ; @return (maps in vector)
  [stored-index _ {:keys [source-paths]}]
  (letfn [(f0 [%] (vector/contains-item? source-paths (:source-path %)))]
         (vector/keep-items-by stored-index f0)))

(defn filter-updated-index
  ; @ignore
  ;
  ; @description
  ; Removes source paths from the given 'updated-index' that are not present in the given 'stored-index',
  ; to prevent misunderstanding declaration changes when a source path is indexed for the first time,
  ; and it has no entry in the stored index (yet).
  ;
  ; @param (maps in vector) stored-index
  ; @param (maps in vector) updated-index
  ; @param (map) options
  ;
  ; @return (maps in vector)
  [stored-index updated-index _]
  (letfn [(f0 [%] (when (source-path-not-indexed? stored-index                   (:source-path %))
                        (println core.messages/MISSING-SOURCE-PATH-INDEX-WARNING (:source-path %))
                        (-> :filter-not-indexed-source-path)))]
         (vector/remove-items-by updated-index f0)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn derive-removed-declarations
  ; @ignore
  ;
  ; @param (maps in vector) stored-index
  ; @param (maps in vector) updated-index
  ; @param (strings in vector) added
  ; @param (map) file-data
  ;
  ; @return (strings in vector)
  [_ updated-index removed {:keys [declarations filepath ns-name] :as file-data}]
  (letfn [(f0 [removed name]
              (if (declaration-not-indexed? updated-index filepath name)
                  (vector/conj-item removed (str ns-name "/" name))
                  (-> removed)))]
         (reduce f0 removed declarations)))

(defn derive-added-declarations
  ; @ignore
  ;
  ; @param (maps in vector) stored-index
  ; @param (maps in vector) updated-index
  ; @param (strings in vector) added
  ; @param (map) file-data
  ;
  ; @return (strings in vector)
  [stored-index _ added {:keys [declarations filepath ns-name] :as file-data}]
  (letfn [(f0 [added name]
              (if (declaration-not-indexed? stored-index filepath name)
                  (vector/conj-item added (str ns-name "/" name))
                  (-> added)))]
         (reduce f0 added declarations)))

(defn derive-changes
  ; @ignore
  ;
  ; @param (maps in vector) stored-index
  ; @param (maps in vector) updated-index
  ; @param (map) options
  ;
  ; @return (map)
  ; {:added (strings in vector)
  ;  :removed (strings in vector)}
  [stored-index updated-index options]
  (let [stored-index  (filter-stored-index  stored-index updated-index options)
        updated-index (filter-updated-index stored-index updated-index options)]
       (letfn [(f0 [added]               (reduce f2 added   updated-index))
               (f1 [removed]             (reduce f3 removed stored-index))
               (f2 [added   source-data] (reduce f4 added   (:source-files source-data)))
               (f3 [removed source-data] (reduce f5 removed (:source-files source-data)))
               (f4 [added   file-data]   (derive-added-declarations   stored-index updated-index added   file-data))
               (f5 [removed file-data]   (derive-removed-declarations stored-index updated-index removed file-data))]
              {:added   (f0 [])
               :removed (f1 [])})))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn remove-outdated-source-paths
  ; @ignore
  ;
  ; @param (maps in vector) stored-index
  ; @param (maps in vector) updated-index
  ;
  ; @return (maps in vector)
  [stored-index updated-index]
  (letfn [(f0 [source-data]   (vector/any-item-matches? updated-index #(f1 source-data %)))
          (f1 [source-data %] (= (:source-path %) (:source-path source-data)))]
         (-> stored-index (vector/remove-items-by f0))))

(defn add-updated-source-paths
  ; @ignore
  ;
  ; @param (maps in vector) stored-index
  ; @param (maps in vector) updated-index
  ;
  ; @return (maps in vector)
  [stored-index updated-index]
  (vector/concat-items stored-index updated-index))
