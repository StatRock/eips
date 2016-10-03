(ns site.core
  (:require [hiccup.page :as hp]
            [io.perun.core :as perun]
            [boot.util :as u]))

(def expected-keys [:content :extension :file-type
                    :filename :full-path :mime-type
                    :original :parent-path :path :short-filename
                    :style :title])
(def required-keys [:title])

(def ensure-has-keys [data expected])

(defn template [data]
  (hp/html5
    [:div
     (-> data :entry :content)]))

(defn no-template [data]
  (-> data :entry :content))

(defn page [data]
  (clojure.pprint/pprint data)
  (case (or (-> data :entry :template) :default)
    :default (template data))
  )
