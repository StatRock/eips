(ns site.advancements
  (:require [io.perun :as perun]
            [boot.core :refer [deftask]]
            [clojure.string :as string]))

(defn- normalize-path [path]
  (string/replace path "\\" "/"))

(defn- advancement-path [file-name]
  (let [path-parts (string/split (normalize-path file-name) #"/")]
    (str (first path-parts) "/" (second path-parts) ".html")))

(deftask advancements
  "Generates advancement pages from the advancement images stored in content/advancements using  templates in templates/advancements.  The output from this task should then be rendered using the default rendering template."
  []
  (comp
    (perun/assortment
      :extensions [".jpg" ".png" ".jpeg"]
      :filterer #(re-find #"^advancements/" (normalize-path (:path %)))
      :grouper (fn [entries]
                 (reduce (fn [group-accumulator entry]
                           (let [path (advancement-path (:path entry))]
                             (update-in group-accumulator [path :entries] conj entry)))
                         {} entries))
      :renderer 'site.advancements-renderer/render-assortment
      :out-dir "")))
