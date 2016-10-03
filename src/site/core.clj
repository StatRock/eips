(ns site.core
  (:require [hiccup.page :as hp]
            [io.perun.core :as perun]
            [boot.util :as u]
            [clojure.set :as set]))

(def perun-keys #{:content :extension :file-type
                :filename :full-path :mime-type
                :original :parent-path :path :short-filename})
(def known-yaml-keys #{:styles :title :nav-right-select
                       :header-image :style-sheet :no-right-matter})

(def required-yaml-keys  #{:title})

(def permitted-default-template-keys
  (set/union perun-keys known-yaml-keys))

(defn enforce-has-keys [data expected]
  (let [missing-keys (set/difference expected (set (keys data)))]
    (if-not (empty? missing-keys)
      (u/fail (str "Warning file: '" (:path data) "' is missing the following required metadata keys: " missing-keys "\n")))))

(defn enforce-only-permitted-keys [data permitted]
  (let [unexpected-keys (set/difference (set (keys data)) permitted)]
    (if-not (empty? unexpected-keys)
      (u/fail (str "Encountered unknown metadata keys in '" (:path data) "': " unexpected-keys "\n")))))

(defn template [data]
  (enforce-has-keys data required-yaml-keys)
  (enforce-only-permitted-keys data permitted-default-template-keys)
  (hp/html5
    [:div
     (:content data)]))

(defn no-template [data]
  (enforce-only-permitted-keys data (set/union perun-keys #{:template}))
  (:content data))

(defn page [{data :entry}]
  (case (or (keyword (:template data)) :default)
    :none (no-template data)
    :default (template data)))
