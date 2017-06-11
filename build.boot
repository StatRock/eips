(set-env!
 :source-paths #{"src" "content" "templates"}
 :dependencies '[[perun "0.4.2-SNAPSHOT"]
                 [hiccup "1.0.5"]
                 [pandeiro/boot-http "0.7.0"]
                 [adzerk/boot-reload "0.4.12"]
                 [deraen/boot-livereload "0.1.2"]
                 [circleci/clj-yaml "0.5.5"]
                 [selmer "1.0.9"]])

(require '[io.perun :refer :all]
         '[io.perun.meta :as meta]
         '[pandeiro.boot-http :as http]
         '[adzerk.boot-reload :refer [reload]]
         '[site.advancements :refer [advancements]]
         '[boot.core :as boot :refer [deftask]]
         '[deraen.boot-livereload :as lr]
         '[clojure.string :as string])

(defn extensions-match
  "Builds regular expressions to filter files based on extension.  The extensions are case
  insensitive, and must match the entire extension.

  See http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html"
  [& extensions]
  (map #(re-pattern (str "(?i)" % #"\Z")) extensions))

(deftask copy-static-assets
         "Copies the static assets from the source to the asset files, ensuring that they
         are included in the the output."
         []
         (comp
           (sift :to-asset (extensions-match ".xml" ".thmx"))
           (sift :to-asset (extensions-match ".jpe?g" ".png" ".gif" ".svg" ".bmp" ".tiff?"))
           (sift :to-asset (extensions-match ".pdf" ".docx?" ".pptx?"))
           (sift :to-asset (extensions-match ".css"))
           (sift :to-asset (extensions-match ".js"))))

(deftask render-website
         "Handles default rendering of the EIPS website."
         []
         (comp
           (mime-type)
           (build-date)
           (images-dimensions)
           ; render 404
           #_(watermark)
           (advancements)
           (copy-static-assets)
           (yaml-metadata :extensions [".html" ".htm"])
           (markdown)
           #_(print-meta)                                   ; for debugging.
           (render :renderer 'site.core/page :out-dir "")))

(deftask build
         "Build the Eastern Idaho Photography Society website, placing built files in the public directory"
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
          "live watch of the built website hosted on http://localhost:3000.  Useful to see the results of changes during development."
          []
          (comp
            (watch :verbose true)
            (render-website)
            (target :dir #{"public"})
            ;for cljs
            #_(reload)
            (lr/livereload)     ; doesn't actually reload right now.
            (http/serve :dir "public")))

(deftask serve
         "Serve the website hosted on http://localhost:3000. Keeps the program that displays the website running.  Get out of it with Ctrl C. "
         []
         (comp
           (render-website)
           (target :dir #{"public"})
           (http/serve :dir "public")
           (wait)))
