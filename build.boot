(set-env!
 :source-paths #{"src" "content" "templates"}
 :dependencies '[[perun "0.4.2-SNAPSHOT"]
                 [pandeiro/boot-http "0.7.0"]
                 [adzerk/boot-reload "0.4.12"]
                 [deraen/boot-livereload "0.1.2"]
                 [circleci/clj-yaml "0.5.5"]
                 [selmer "1.0.9"]
                 [hashobject/boot-s3 "0.1.2-SNAPSHOT"]]
  :target-path "public")

(require '[io.perun :as perun]
         '[io.perun.meta :as meta]
         '[pandeiro.boot-http :as http]
         '[adzerk.boot-reload :refer [reload]]
         '[site.advancements :refer [advancements]]
         '[boot.core :as boot :refer [deftask]]
         '[deraen.boot-livereload :as lr]
         '[clojure.string :as string]
         '[clj-yaml.core :as yaml]
         '[hashobject.boot-s3 :refer [s3-sync]]
         '[clojure.pprint :as p]) ; pprint is used to print debug statements with (p/pprint thing)

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
           (perun/mime-type)
           (perun/build-date)
           (perun/images-dimensions)
           ; render 404
           #_(perun/watermark)
           (advancements)
           (copy-static-assets)
           (perun/yaml-metadata :extensions [".html" ".htm"])
           (perun/markdown)
           #_(perun/print-meta)                                   ; for debugging.
           (perun/render :renderer 'site.core/page :out-dir "")))

(deftask build
         "Build the Eastern Idaho Photography Society website, placing built files in the public directory"
         []
         (comp
           (render-website)
           (target :dir #{"public"})))

(deftask deploy-test
         "Build the Eastern Idaho Photography Society website, and upload it to Amazon S3."
         []
         (let [credentials (yaml/parse-string (slurp "aws-credentials.yaml"))
           {:keys [secret-key access-key]} credentials]
           (comp
             (build)
             (s3-sync :bucket "test.eips.net" :source "" :access-key access-key :secret-key secret-key)
             )))

(deftask deploy-prod
         "Build the Eastern Idaho Photography Society website, and upload it to Amazon S3."
         []
         (let [credentials (yaml/parse-string (slurp "aws-credentials.yaml"))
               {:keys [secret-key access-key]} credentials]
            (comp
              (build)
              (s3-sync :bucket "eips.net" :source "" :access-key access-key :secret-key secret-key)
              )))

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
