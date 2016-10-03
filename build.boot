(set-env!
 :source-paths #{"src" "content"}
 :dependencies '[[perun "0.4.0-SNAPSHOT"]
                 [hiccup "1.0.5"]
                 [pandeiro/boot-http "0.7.0"]
                 [adzerk/boot-reload "0.4.12"]
                 [circleci/clj-yaml "0.5.5"]])

(require '[io.perun :refer :all]
         '[pandeiro.boot-http :as http]
         '[adzerk.boot-reload :refer [reload]]
         '[site.html-fragment :as html]
         '[boot.core :as boot :refer [deftask]])

(defn extensions-match [& extensions]
  (map #(re-pattern (str "(?i)" % #"\Z")) extensions))

(deftask advancements
         "handles processing of the advancement images and templates"
         [])



(deftask render-website
         "does the rendering of the website"
         []
         (comp
           #_(base)
           #_(add-extension)
           #_(advancements)
           (sift :to-asset (extensions-match "jpe?g" ".png" ".gif" ".svg" ".bmp" ".tiff?"))
           (sift :to-asset (extensions-match ".xml" ".thmx"))
           (sift :to-asset (extensions-match ".pdf" ".docx?" ".pptx?"))
           (sift :to-asset (extensions-match ".css"))
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
           (build)))


(deftask dev
         "live watch of the built website."
         []
         (comp
           (reload)
           (render-website)
           (http/serve :resource-root "public")
           (watch)
           (wait)))


