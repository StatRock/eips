(ns site.advancements
  (:require [io.perun :as perun-io]
            [boot.core :as boot :refer [deftask]]
            [io.perun.core   :as perun]
            [clojure.java.io :as io]
            [clojure.string  :as str]
            [clj-yaml.core   :as yaml]
            [clojure.walk    :as walk]))

(deftask advancements
  "handles processing of the advancement images and templates"
  []
  )
