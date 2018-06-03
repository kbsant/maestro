(ns maestro.env
  (:require [clojure.tools.logging :as log]
            [maestro.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[maestro started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[maestro has shut down successfully]=-"))
   :middleware wrap-dev})
