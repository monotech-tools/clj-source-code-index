
(ns source-code-index.core.engine
    (:require [source-code-index.map.engine :as map.engine]
              [source-code-index.import.engine :as import.engine]
              [source-code-index.export.engine :as export.engine]
              [source-code-index.core.tests :as core.tests]
              [source-code-index.core.prototypes :as core.prototypes]
              [validator.api :as v]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn index-source-files!
  ; @description
  ; - Reads source files from the given source paths that match the given filename pattern,
  ;   updates the index.edn file on the given index filepath with def and defn declarations,
  ;   updates the CHANGES.md file (compared to the previous index) on the given changes filepath.
  ; - Different source paths can use a shared index file.
  ;
  ; @param (map) options
  ; {:changes-filepath (string)(opt)
  ;   Default: "CHANGES.md"
  ;  :filename-pattern (regex-pattern)(opt)
  ;   Default: #"[a-z\_\d]{1,}\.clj[cs]{0,1}"
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
  (if (v/valid? options core.tests/OPTIONS-TEST {:prefix "options"})
      (let [options (core.prototypes/options-prototype options)]
           (-> [] (map.engine/map-source-paths        options)
                  (import.engine/import-source-paths  options)
                  (export.engine/update-changes-file! options)
                  (export.engine/update-index-file!   options)))))
