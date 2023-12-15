
(ns source-code-index.api
    (:require [source-code-index.core.config :as core.config]
              [source-code-index.core.engine :as core.engine]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @redirect (source-code-index.core.config/*)
(def SOURCE-FILENAME-PATTERN core.config/SOURCE-FILENAME-PATTERN)

; @redirect (source-code-index.core.engine/*)
(def index-source-files! core.engine/index-source-files!)
