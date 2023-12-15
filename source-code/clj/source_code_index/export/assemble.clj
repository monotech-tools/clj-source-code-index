
(ns source-code-index.export.assemble
    (:require [fruits.regex.api :as regex]
              [fruits.string.api :as string]
              [fruits.vector.api :as vector]
              [time.api :as time]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn assemble-added-declarations
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (string) file-content
  ; @param (map) changes
  ;
  ; @return (string)
  [_ _ _ {:keys [added]}]
  (letfn [(f0 [%] (str "\n\n- " % " [added]"))]
         (-> added (vector/->items f0)
                   (string/join))))

(defn assemble-removed-declarations
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (string) file-content
  ; @param (map) changes
  ;
  ; @return (string)
  [_ _ _ {:keys [removed]}]
  (letfn [(f0 [%] (str "\n\n- " % " [removed]"))]
         (-> removed (vector/->items f0)
                     (string/join))))

(defn assemble-changed-declarations
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (string) file-content
  ; @param (map) changes
  ;
  ; @return (string)
  [index options file-content changes]
  (str (assemble-added-declarations   index options file-content changes)
       (assemble-removed-declarations index options file-content changes)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn assemble-na-header
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (string) file-content
  ; @param (map) changes
  ;
  ; @return (string)
  [index {:keys [version]} _ _]
  (let [date (-> index first :indexed-at time/timestamp-string->date)]
       (str "\n### [n/a] - " date)))

(defn assemble-version-header
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (string) file-content
  ; @param (map) changes
  ;
  ; @return (string)
  [index {:keys [version]} _ _]
  (let [date (-> index first :indexed-at time/timestamp-string->date)]
       (str "\n### [" version "] - " date)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn assemble-changes-file
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (string) file-content
  ; @param (map) changes
  ;
  ; @return (string)
  [index {:keys [version] :as options} file-content changes]
  (if-let [last-version (regex/re-last file-content #"(?<=\n\#\s\[).*(?=\]\s\-\s[\d]{4,4}\-[\d]{2,2}\-[\d]{2,2})")]
          (cond (-> version empty?)           (str file-content (assemble-changed-declarations index options file-content changes))
                (-> version (= last-version)) (str file-content (assemble-changed-declarations index options file-content changes))
                :use-version                  (str file-content (assemble-version-header       index options file-content changes)
                                                                (assemble-changed-declarations index options file-content changes)))
          (cond (-> version empty?)           (str file-content (assemble-na-header            index options file-content changes)
                                                                (assemble-changed-declarations index options file-content changes))
                :use-version                  (str file-content (assemble-version-header       index options file-content changes)
                                                                (assemble-changed-declarations index options file-content changes)))))
