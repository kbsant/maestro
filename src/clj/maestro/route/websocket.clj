(ns maestro.route.websocket
  (:require [compojure.core :refer [GET defroutes]]
            [maestro.controller.message :as message]))

(defroutes websocket-routes
  (GET "/ws/message" [] message/ws-handler))
