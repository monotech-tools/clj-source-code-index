
(ns source-code-index.export.assemble
    (:require [fruits.regex.api                :as regex]
              [fruits.string.api               :as string]
              [fruits.vector.api               :as vector]
              [io.api                          :as io]
              [source-code-index.export.config :as export.config]
              [time.api                        :as time]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn assemble-added-declarations
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (map) changes
  ;
  ; @return (string)
  [_ _ {:keys [added]}]
  (letfn [(f0 [%] (str "\n\n- " % " [added]"))]
         (-> added (vector/->items f0)
                   (string/join))))

(defn assemble-removed-declarations
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (map) changes
  ;
  ; @return (string)
  [_ _ {:keys [removed]}]
  (letfn [(f0 [%] (str "\n\n- " % " [removed]"))]
         (-> removed (vector/->items f0)
                     (string/join))))

(defn assemble-changed-declarations
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (map) changes
  ;
  ; @return (string)
  [index options changes]
  (str (assemble-added-declarations   index options changes)
       (assemble-removed-declarations index options changes)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn assemble-na-header
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (map) changes
  ;
  ; @return (string)
  [index {:keys [version]} _]
  (let [date (-> index first :indexed-at time/timestamp-string->date)]
       (str "\n\n### [n/a] - " date)))

(defn assemble-version-header
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (map) changes
  ;
  ; @return (string)
  [index {:keys [version]} _]
  (let [date (-> index first :indexed-at time/timestamp-string->date)]
       (str "\n\n### [" version "] - " date)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn assemble-changes-file
  ; @ignore
  ;
  ; @param (maps in vector) index
  ; @param (map) options
  ; @param (map) changes
  ;
  ; @return (string)
  [index {:keys [changes-filepath version] :as options} changes]
  (let [file-content (io/read-file changes-filepath {:warn? false})]
       (if-let [last-version (regex/re-last file-content export.config/VERSION-PATTERN)]
               (cond (-> version empty?)           (str file-content (assemble-changed-declarations index options changes))
                     (-> version (= last-version)) (str file-content (assemble-changed-declarations index options changes))
                     :use-version                  (str file-content (assemble-version-header       index options changes)
                                                                     (assemble-changed-declarations index options changes)))
               (cond (-> version empty?)           (str file-content (assemble-na-header            index options changes)
                                                                     (assemble-changed-declarations index options changes))
                     :use-version                  (str file-content (assemble-version-header       index options changes)
                                                                     (assemble-changed-declarations index options changes))))))
