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

(deftask render-website
         "does the rendering of the website"
         []
         (comp
           #_(base)
           #_(add-extension)
           (markdown)
           (html/html-fragments)
           (print-meta)
           (render :renderer 'site.core/page)))

(deftask build
         "Build the Eastern Idaho Photography Society website"
         []
         (comp
           (render-website)
           (target)))

(deftask deploy
         "build the Eastern Idaho Photography Society website, and upload it to the server."
         []
         (comp
           (render-website)
           (target)))


(deftask dev
         "live watch of the built website."
         []
         (comp
           (reload)
           (render-website)
           (http/serve :resource-root "public")
           (watch)
           (wait)))


