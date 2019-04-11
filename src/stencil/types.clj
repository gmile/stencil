(ns stencil.types
  (:import [clojure.lang IDeref])
  (:require [clojure.pprint])
  (:gen-class))

(set! *warn-on-reflection* true)

(def open-tag "{%")
(def close-tag "%}")

(defrecord OpenTag [open])
(defmethod clojure.pprint/simple-dispatch OpenTag [t] (print (str "<" (:open t) ">")))

(defrecord CloseTag [close])
(defmethod clojure.pprint/simple-dispatch CloseTag [t] (print (str "</" (:close t) ">")))

(defrecord TextTag [text])
(defmethod clojure.pprint/simple-dispatch TextTag [t] (print (str "'" (:text t) "'")))

(defn ->text [t] (->TextTag t))
(defn ->close [t] (->CloseTag t))
(def ->open ->OpenTag)

(defprotocol ControlMarker)

;; Invocation of a fragment by name
(defrecord FragmentInvoke [result] ControlMarker)

;; egyedi parancs objektumok

;; ez a marker jeloli, hogy egy oszlopot el kell rejteni.
(defrecord HideTableColumnMarker [columns-resize] ControlMarker)

(def column-resize-modes #{:resize-first :resize-last :rational :cut})

(defn ->HideTableColumnMarker
  ([] (HideTableColumnMarker. :cut))
  ([x] (assert (column-resize-modes x))
       (HideTableColumnMarker. x)))

;; ez a marker jeloli, hogy egy egesz sort el kell rejteni.
(defrecord HideTableRowMarker [] ControlMarker)

(defn hide-table-column-marker? [x] (instance? HideTableColumnMarker x))
(defn hide-table-row-marker? [x] (instance? HideTableRowMarker x))

;; Function calls might return delayed values that are dereferenced
;; only in the postprocess stage.
(defrecord DelayedValueMarker [delay-object]
  IDeref
  (deref [_] @delay-object))

(defn control? [x] (satisfies? ControlMarker x))
