
(ns source-code-index.core.prototypes
    (:require [fruits.vector.api :as vector]
              [io.api :as io]
              [source-code-index.core.config :as core.config]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn options-prototype
  ; @ignore
  ;
  ; @param (map) options
  ;
  ; @return (map)
  ; {:filename-pattern (regex pattern)
  ;  :output-path (string)
  ;  :source-paths (strings in vector)}
  [options]
  (-> {:changes-filepath core.config/DEFAULT-CHANGES-FILEPATH
       :filename-pattern core.config/DEFAULT-FILENAME-PATTERN
       :index-filepath   core.config/DEFAULT-INDEX-FILEPATH}
      (merge options)
      (update :changes-filepath            io/valid-absolute-path)
      (update :index-filepath              io/valid-absolute-path)
      (update :source-paths vector/->items io/valid-absolute-path)))
