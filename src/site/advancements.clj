(ns site.advancements
  (:require [io.perun :as perun-io]
            [boot.core :as boot :refer [deftask]]
            [io.perun.core   :as perun]
            [clojure.java.io :as io]
            [clojure.string  :as str]
            [clj-yaml.core   :as yaml]
            [clojure.walk    :as walk]))


(defn -file-changes [filter-fn prev-fs fileset]
  (let [new-files (->> fileset
                       (boot/fileset-diff prev-fs)
                       boot/user-files
                       filter-fn
                       perun-io/add-filedata)
        removed (->> fileset
                     (boot/fileset-removed prev-fs)
                     boot/user-files
                     filter-fn
                     (map #(boot/tmp-path %))
                     set)]
    [new-files removed]))


(defn make-advancement-set [image-files]
  )

(defn process-advancements [advancement-sets]
  )

(deftask advancements
  "handles processing of the advancement images and templates"
  []
  (let [prev-meta (atom {})
        prev-fs (atom nil)]
    (boot/with-pre-wrap fileset
      (let [[advancement-images removed-images] (-file-changes #(boot/by-ext ["advancements"] %)
                                                               @prev-fs fileset)
            advancement-sets (make-advancement-set advancement-images)
            removed-sets (filter
                           #(contains? (map :url advancement-sets) (:url %))
                           (make-advancement-set removed-images))
            advancement-pages (process-advancements advancement-sets)
            initial-metadata (perun/merge-meta* (perun/get-meta fileset) @prev-meta)
            ; Pure merge instead of `merge-with merge` (meta-meta).
            ; This is because updated metadata should replace previous metadata to
            ; correctly handle cases where a metadata key is removed from post metadata.
            final-metadata (vals (merge (perun/key-meta initial-metadata) (perun/key-meta advancement-pages)))
            final-metadata (remove #(-> % :path removed-sets) final-metadata)
            ; todo remove the generated image pages as well.
            ]
        (reset! prev-fs fileset)
        (reset! prev-meta final-metadata)
        (perun/set-meta fileset final-metadata)))))
