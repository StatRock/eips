(set-env!
 :source-paths #{"src" "content" "templates"}
 :dependencies '[[perun "0.4.0-SNAPSHOT"]
                 [hiccup "1.0.5"]
                 [pandeiro/boot-http "0.7.0"]
                 [adzerk/boot-reload "0.4.12"]
                 [deraen/boot-livereload "0.1.2"]
                 [circleci/clj-yaml "0.5.5"]
                 [selmer "1.0.9"]])

(require '[io.perun :refer :all]
         '[pandeiro.boot-http :as http]
         '[adzerk.boot-reload :refer [reload]]
         '[site.html-fragment :as html]
         '[site.advancements :refer [advancements]]
         '[boot.core :as boot :refer [deftask]]
         '[deraen.boot-livereload :as lr])

(defn extensions-match [& extensions]
  (map #(re-pattern (str "(?i)" % #"\Z")) extensions))

(deftask render-website
         "does the rendering of the website"
         []
         (comp
           #_(base)
           #_(watermark)
           #_(advancements)
           (sift :to-asset (extensions-match ".xml" ".thmx"))
           (sift :to-asset (extensions-match ".jpe?g" ".png" ".gif" ".svg" ".bmp" ".tiff?"))
           (sift :to-asset (extensions-match ".pdf" ".docx?" ".pptx?"))
           (sift :to-asset (extensions-match ".css"))
           (sift :to-asset (extensions-match ".js"))
           (markdown)
           (html/html-fragments)
           #_(print-meta)
           (render :renderer 'site.core/page :out-dir "")))

(deftask build
         "Build the Eastern Idaho Photography Society website"
         []
         (comp
           (render-website)
           (target :dir #{"public"})))

(deftask deploy
         "build the Eastern Idaho Photography Society website, and upload it to the server."
         []
         (comp
           (build)
           #_(sftp)))


(deftask dev
         "live watch of the built website."
         []
         (comp
           (watch :verbose true)
           (render-website)                                 ;for cljs
           #_(reload)
           (lr/livereload)
           (http/serve :resource-root "")))
