(ns site.advancements
  (:require [io.perun :as perun]
            [boot.core :as boot :refer [deftask]]
            [io.perun.core :as perun-core]
            [boot.util :as u]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-yaml.core :as yaml]
            [clojure.walk :as walk]
            [selmer.parser :as selmer]
            [clojure.string :as string]))

(defn- normalize-path [path]
  (string/replace path "\\" "/"))

(defn- render-assortment [{data :entry}]
  (u/info (clojure.pprint/write data :stream nil))
  #_(selmer/render-file "advancement/advancement_indexes.html_template" data))

(defn- advancement-path [file-name]
  (let [path-parts (str/split "/" (normalize-path file-name))]
    (str (first path-parts) "/" (second path-parts) "/index.html")))

(defn- advancement-name [file-name]
  (second (str/split "/" (normalize-path file-name))))


(deftask advancements
  "Generates advancement pages from the advancement images stored in content/advancements using
  templates in templates/advancements.  The output from this task should then be rendered using
  the default rendering template."
  []
  (comp
    #_(perun/assortment
      :extensions [".jpg" ".png" ".jpeg"]
      :filterer #(re-find #"^/advancements/" (normalize-path (:path %)))
      :grouper #(advancement-path (:path %))        ; this needs to be a reducer... too bad that there isn't just a key function....
      :renderer 'site.advancements/render-assortment)))
