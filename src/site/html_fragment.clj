(ns site.html-fragment
  (:require [io.perun :as perun-io]
            [boot.core :as boot :refer [deftask]]
            [io.perun.core   :as perun]
            [clojure.java.io :as io]
            [clojure.string  :as str]
            [clj-yaml.core   :as yaml]
            [clojure.walk    :as walk])
  (:import [flatland.ordered.map OrderedMap]
           [flatland.ordered.set OrderedSet]))

(def ^:dynamic *yaml-head* #"---\r?\n")

(defn substr-between
  "Find string that is nested in between two strings. Return first match.
  Copied from https://github.com/funcool/cuerdas"
  [s prefix suffix]
  (cond
    (nil? s) nil
    (nil? prefix) nil
    (nil? suffix) nil
    :else
    (some-> s
            (str/split prefix)
            second
            (str/split suffix)
            first)))

(defn normal-colls
  "Clj-yaml keeps order of map properties by using ordered maps. These are inconvenient
  for us as the ordered library is not necessarily available in other pods."
  [x]
  (walk/postwalk
    (fn [y]
      (cond
        (instance? OrderedMap y) (into {} y)
        (instance? OrderedSet y) (into #{} y)
        :else y))
    x))

(defn parse-file-metadata [file-content]
  (if-let [metadata-str (substr-between file-content *yaml-head* *yaml-head*)]
    (if-let [parsed-yaml (normal-colls (yaml/parse-string metadata-str))]
      ; we use `original` file flag to distinguish between generated files
      ; (e.x. created those by plugins)
      (assoc parsed-yaml :original true)
      {:original true})
    {:original true}))

(defn remove-metadata [content]
  (let [splitted (str/split content *yaml-head* 3)]
    (if (> (count splitted) 2)
      (first (drop 2 splitted))
      content)))

(defn read-html [file-content]
  (->> file-content
       remove-metadata))

(defn process-file [file]
  (perun/report-debug "html" "processing html" (:filename file))
  (let [file-content (-> file :full-path io/file slurp)
        md-metadata (parse-file-metadata file-content)
        html (read-html file-content)]
    (merge md-metadata {:content html} file)))

(defn parse-html [markdown-files]
  (let [updated-files (doall (map #(process-file %) markdown-files))]
    (perun/report-info "html" "parsed %s html files" (count markdown-files))
    updated-files))


(deftask html-fragments
         "parse html fragments  that may be headed with yaml metadata"
         []
         (let [prev-meta (atom {})
               prev-fs   (atom nil)]
           (boot/with-pre-wrap fileset
             (let [html-files (->> fileset
                                 (boot/fileset-diff @prev-fs)
                                 boot/user-files
                                 (boot/by-ext ["htm" "html"])
                                 perun-io/add-filedata)
                   ; process all removed markdown files
                   removed? (->> fileset
                                 (boot/fileset-removed @prev-fs)
                                 boot/user-files
                                 (boot/by-ext ["htm" "html"])
                                 (map #(boot/tmp-path %))
                                 set)
                   updated-files (parse-html html-files)
                   initial-metadata (perun/merge-meta* (perun/get-meta fileset) @prev-meta)
                   ; Pure merge instead of `merge-with merge` (meta-meta).
                   ; This is because updated metadata should replace previous metadata to
                   ; correctly handle cases where a metadata key is removed from post metadata.
                   final-metadata   (vals (merge (perun/key-meta initial-metadata) (perun/key-meta updated-files)))
                   final-metadata   (remove #(-> % :path removed?) final-metadata)]
               (reset! prev-fs fileset)
               (reset! prev-meta final-metadata)
               (perun/set-meta fileset final-metadata)))))

