(ns site.core
  "The basic rendering logic for the EIPS website.  Pages are routed by the page function to
  the correct template handling function.  Each template handling function first checks
  to make sure that the metadata keys are appropriate, and then executes the selmer template
  located in the templates directory.

  If you want to add a new template, first, add a new rendering function (I would copy the
  template function, adjust the expected metadata keys, and call the new template), then add
  a new clause to the case statement in the page function with the correct key that calls
  your new template function."
  (:require [hiccup.page :as hp]
            [selmer.parser :as selmer]
            [boot.util :as u]
            [clojure.set :as set]))


; sets of expected keys, perhaps with documentation.

(def perun-keys
  "These are the known metadata keys that Perun adds to the metadata.

  See https://github.com/hashobject/perun/blob/master/SPEC.md"
  #{:content :extension :file-type
    :filename :full-path :mime-type
    :original :parent-path :path :short-filename
    :slug :permalink :out-dir :original-path :io.perun/trace
    :date-build })

(def known-yaml-keys
  "These are metadata keys that we use, listed here to protect from misspellings, and describe
  their purpose."
  {:title "The title for the page.  It will be displayed in the browser title bar."
   :styles "Includes extra text to be placed in the page's stylesheet."
   :header-image "The image which should be displayed in the header div."
   :style-sheet "includes a link to this additional stylesheet."
   :nav-right-select "Fixes the right nav content rather than having it randomly chosen by
                      javascript."
   :no-right-matter "prevents the right matter from rendering when set to true."
   })

(def required-yaml-keys
  "These are keys that the template requires to render correctly."
  #{:title})

(def permitted-default-template-keys
  (set/union perun-keys (keys known-yaml-keys)))


; Utility functions to do error checking

(defn- enforce-has-keys [data expected]
  "this is used to make sure that data essential for rendering is always included.  If it triggers
  an error, you probably need to add the missing key to the YAML metadata for the page in question."
  (let [missing-keys (set/difference expected (set (keys data)))]
    (if-not (empty? missing-keys)
      (u/fail (str "site.core/enforce-has-keys: File: '" (:path data) "' is missing the following needed metadata keys: " missing-keys "\n")))))

(defn- enforce-only-permitted-keys
  "In odrder to protect against mispelling of metadata keys, this ensures that the render is aware
  aware of all the metadata keys used.  If this causes a failure on a key that isn't the result of
  a spelling error, please add that key to the appropriate list above."
  [data permitted]
  (let [unexpected-keys (set/difference (set (keys data)) permitted)]
    (if-not (empty? unexpected-keys)
      (u/fail (str "site.core/enforce-only-permitted-keys: Unknown metadata keys (spelling error?) in '" (:path data) "': " unexpected-keys "\n")))))

; template rendering functions

(defn template
  "The default template handler.  Uses Selmer for rendering"
  [data]
  (enforce-has-keys data required-yaml-keys)
  (enforce-only-permitted-keys data permitted-default-template-keys)
  (selmer/render-file "basic_template.html_template" data))

(defn no-template
  "Handles rendering for pages that are compete by themselves."
  [data]
  (enforce-only-permitted-keys data (set/union perun-keys #{:template}))
  (:content data))

; template routing function.

(defn page
  "Dispatches page rendering to template functions based on the :template metadata key."
  [{data :entry}]
  (case (or (keyword (:template data)) :default)
    :none (no-template data)
    :default (template data)))
