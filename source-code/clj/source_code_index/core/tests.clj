
(ns source-code-index.core.tests
    (:require [fruits.regex.api :as regex]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @ignore
;
; @note
; https://github.com/bithandshake/cljc-validator
;
; @constant (map)
(def OPTIONS-TEST
     {:changes-filepath {:opt* true  :f* string?             :not* empty? :e* ":changes-filepath must be a nonempty string!"}
      :filename-pattern {:opt* true  :f* regex/pattern?                   :e* ":filename-pattern must be a regex pattern!"}
      :index-filepath   {:opt* true  :f* string?             :not* empty? :e* ":index-filepath must be a nonempty string!"}
      :source-paths     {:and* [vector? #(every? string? %)] :not* empty? :e* ":source-paths must be a nonempty vector with string items!"}})
