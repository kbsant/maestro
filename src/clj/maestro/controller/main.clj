(ns maestro.controller.main
  (:require
    [maestro.config :refer [env]]
    [maestro.layout :as layout]
    [maestro.view.main :as view.main]))

(defn render []
  (layout/render-hiccup view.main/render {:glossary {:appname "Anketeur" :title "Survey" :message (:app-message env)}}))
