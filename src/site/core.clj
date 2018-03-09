(ns site.core
  "The basic rendering logic for the EIPS website.  Pages are routed by the page function to the correct template handling function.  Each template handling function first checks to make sure that the metadata keys are appropriate, and then executes the selmer template located in the templates directory.
  If you want to add a new template, first, add a new rendering function (I would copy the  template function, adjust the expected metadata keys, and call the new template), then add a new clause to the case statement in the page function with the correct key that calls your new template function."
  (:require [hiccup.page :as hp]
            [selmer.parser :as selmer]
            [boot.util :as u]
            [clojure.set :as set]
            [clojure.pprint :as pprint]
            [clojure.java.io :as io]))

; sets of expected keys, perhaps with documentation.

(def perun-keys
  "These are the known metadata keys that Perun adds to the metadata.
  See https://github.com/hashobject/perun/blob/master/SPEC.md"
  #{:content :extension :file-type
    :filename :full-path :mime-type
    :original :parent-path :path :short-filename
    :slug :permalink :out-dir :original-path :io.perun/trace
    :date-build})

(def known-yaml-keys
  "These are metadata keys that we use, listed here to protect from misspellings, and describe
  their purpose."
  {:title "The title for the page.  It will be displayed in the browser title bar."
   :styles "Includes extra text to be placed in the page's stylesheet."
   :header-image "The image which should be displayed in the header div."
   :style-sheet "includes a link to this additional stylesheet."
   :nav-right-select "Fixes the right nav content rather than having it randomly chosen by javascript."
   :no-right-matter "prevents the right matter from rendering when set to true."
   :template "the file name of the template to be used to render the file (without the .html_template extension), or none.  If none, then selmer will not process the file."
   })

(def required-yaml-keys
  "These are keys that various templates require to render correctly."
  {"basic_template" #{:title}})

(def permitted-template-keys
  "these are various keys that a particular template knows how to handle."
  {"basic_template" (keys known-yaml-keys)})

(def yaml-defaults
  "These are default values for keys that will be used to render pages"
  {:header-image "oxbow"
   :template     "basic_template"})

(defn- to-template-file-name [file-name]
  (str file-name ".html_template"))

(defn- do-render [template-file data]
  (if-not (io/resource template-file)
    (u/fail (str "failed to render '" (:path data) "' because expected template '" template-file "' was not found.\n"))
    (selmer/render-file template-file data)))

(def key-transforms
  "These are transformations that will be applied to certain keys for certain templates before rendering.
  Note: because of how Selmer currently 'memoizes' (stores for future use) templates, the 'targets of includes' (things that will be assembled together in some output) must be known at compile time and cannot be determined based on data.  Because of that, we are including the rendering in the data transform."
  {"basic_template" (fn [data] (-> data
                                 (update-in [:header-image] #(do-render (str "header_images/" (to-template-file-name %)) data))))})

; Utility functions to do error checking

(defn- enforce-has-keys [data expected]
  "this check is used to make sure that data essential for rendering is always included.  If it triggers  an error, you probably need to add the missing key to the YAML metadata for the page in question."
  (let [missing-keys (set/difference expected (set (keys data)))]
    (if-not (empty? missing-keys)
      (u/fail (str "site.core/enforce-has-keys: File: '" (:path data) "' is missing the following needed metadata keys: " missing-keys "\n")))))

(defn- enforce-only-permitted-keys
  "In order to protect against mispelling of metadata keys, this check ensures that the render is aware of all the metadata keys used.  If this causes a failure on a key that isn't the result of a spelling error, please add that key to the appropriate list above."
  [data permitted]
  (let [unexpected-keys (set/difference (set (keys data)) permitted)]
    (if-not (empty? unexpected-keys)
      (u/fail (str "site.core/enforce-only-permitted-keys: Unknown metadata keys (spelling error?) in '" (:path data) "': " unexpected-keys "\n")))))

; template rendering functions

(defn template
  "The default template handler.  Uses Selmer for rendering"
  [template data]
  (let [required-keys (get required-yaml-keys template)
        permitted-keys (set/union perun-keys (get permitted-template-keys template))
        template-file (to-template-file-name template)
        data-transformer (or (get key-transforms template) identity)
        updated-data (data-transformer data)]
    (enforce-has-keys data required-keys)
    (enforce-only-permitted-keys data permitted-keys)
    (do-render template-file updated-data)))

(defn no-template
  "Handles rendering for pages that are compete by themselves."
  [data]
  (enforce-only-permitted-keys data (set/union perun-keys (keys yaml-defaults)))
  (:content data))

; template routing function.

(defn page
  "Dispatches page rendering to template functions based on the :template metadata key."
  [{data :entry}]
  (let [data-with-defaults (merge yaml-defaults data)
        template-name (:template data-with-defaults)]
    (if (= template-name "none")
      (no-template data-with-defaults)
      (template template-name data-with-defaults))))
