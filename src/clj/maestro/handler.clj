(ns maestro.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [maestro.layout :refer [error-page]]
            [maestro.route.home :refer [home-routes]]
            [maestro.route.websocket :refer [websocket-routes]]
            [compojure.route :as route]
            [maestro.env :refer [defaults]]
            [mount.core :as mount]
            [maestro.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    #'websocket-routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))

(defn app [] (middleware/wrap-base #'app-routes))
