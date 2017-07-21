(ns site.advancements-renderer
  (:require [selmer.parser :as selmer]
            [boot.util :as u]
            [clojure.string :as string]))

(defn- normalize-path [path]
  (string/replace path "\\" "/"))

(defn strip-content [entries]
  (map #(dissoc % :content) entries))
  
(defn- advancement-name [file-name]
  (-> file-name
    (normalize-path)
    (string/split #"/")
    (second)
    (string/replace #".html?$" "")))
  
(def ^:private month-abbrs-to-month
  { "feb" "February"
    "mar" "March"
    "april" "April"
    "may" "May"
    "june" "June"
    "july" "July"
    "aug" "August"
    "sept" "September"
    "oct" "October"
    "nov" "November"
    "dec" "December"})
   

(defn- year-abbrs-to-year [year_desc]
  (if (< 1 (count year_desc))
    ({ ["1" "2011"] "2011"
       ["2" "2011"] "2011 (second session)"} year_desc)
    (first year_desc)))

(defn- advancement-title [advancement-name]
  (let [[_ month & year] (string/split advancement-name #"_")]
    (str "Images for " (month-abbrs-to-month month) ", " (year-abbrs-to-year year))))

(defn render-assortment [{{:keys [path] :as entry} :entry :keys [entries]}]
  (let [processed-entries (strip-content entries)
        advancement {:advancement-title (advancement-name path)
                     :images            (map #(normalize-path (:path %)) entries)
                     :title             (advancement-title (advancement-name path))}]
    (selmer/render-file "advancement/advancement_indexs.html_template" advancement)))
