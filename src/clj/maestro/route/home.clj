(ns maestro.route.home
  (:require
    [maestro.controller.message :as message]
    [compojure.core :refer [defroutes GET POST]]))

(defroutes home-routes
  (GET "/" request (message/render request))
  (GET "/message" request (message/render request)))

