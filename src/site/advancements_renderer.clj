(ns site.advancements-renderer
  (:require [selmer.parser :as selmer]
            [boot.util :as u]
            [clojure.string :as string]))

(defn- normalize-path [path]
  (string/replace path "\\" "/"))

(defn strip-content [entries]
  (map #(dissoc % :content) entries))

(defn- advancement-name [file-name]
  (second (string/split "/" (normalize-path file-name))))

(defn render-assortment [{{:keys [path] :as entry} :entry :keys [entries]}]
  (let [processed-entries (strip-content entries)
        advancement {:advancement-title path #_(advancement-name path)
                     :images            (map #(:path %) entries)}]
    (selmer/render-file "advancement/advancement_indexs.html_template" advancement)))
