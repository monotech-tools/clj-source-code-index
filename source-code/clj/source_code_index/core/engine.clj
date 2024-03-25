
(ns source-code-index.core.engine
    (:require [source-code-index.core.prototypes :as core.prototypes]
              [source-code-index.core.tests      :as core.tests]
              [source-code-index.export.engine   :as export.engine]
              [source-code-index.import.engine   :as import.engine]
              [source-code-index.map.engine      :as map.engine]
              [validator.api                     :as v]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn index-source-files!
  ; @description
  ; 1. Reads source files from the given source paths that match the given filename pattern.
  ; 2. Updates the 'source-code-index.edn' file at the given index filepath (stores def and defn declarations).
  ; 3. Updates the 'CHANGES.md' file (the changes derived by comparing the current and the previous index) at the given changes filepath.
  ;
  ; @note
  ; Different source paths can use a shared index file.
  ;
  ; @param (map) options
  ; {:changes-filepath (string)(opt)
  ;   Default: "CHANGES.md"
  ;  :filename-pattern (regex-pattern)(opt)
  ;   Default: #"[a-z\_\d]+\.clj[cs]?"
  ;  :index-filepath (string)(opt)
  ;   Default: "source-code-index.edn"
  ;  :source-paths (strings in vector)
  ;  :version (string)(opt)}
  ;
  ; @usage
  ; (index-source-files! {:filename-pattern #"[a-z\_]\.clj"
  ;                       :source-paths     ["source-code"]
  ;                       :version          "1.2.3"})
  [options]
  (if (v/valid? options [core.tests/OPTIONS-TEST] {:prefix "options"})
      (let [options (core.prototypes/options-prototype options)]
           (-> [] (map.engine/map-source-paths        options)
                  (import.engine/import-source-paths  options)
                  (export.engine/update-changes-file! options)
                  (export.engine/update-index-file!   options)))))
