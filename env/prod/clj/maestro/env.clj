(ns maestro.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[maestro started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[maestro has shut down successfully]=-"))
   :middleware identity})
