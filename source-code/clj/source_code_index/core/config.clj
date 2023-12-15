
(ns source-code-index.core.config)

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @constant (regex pattern)
(def SOURCE-FILENAME-PATTERN #"[a-z\_\d]{1,}\.clj[cs]{0,1}")

; @ignore
;
; @constant (regex pattern)
(def DEFAULT-FILENAME-PATTERN SOURCE-FILENAME-PATTERN)

; @ignore
;
; @constant (regex pattern)
(def DEFAULT-CHANGES-FILEPATH "CHANGES.md")

; @ignore
;
; @constant (regex pattern)
(def DEFAULT-INDEX-FILEPATH "source-code-index.edn")
