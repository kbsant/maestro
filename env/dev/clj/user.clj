(ns user
  (:require [mount.core :as mount]
            [maestro.figwheel :refer [start-fw stop-fw cljs]]
            maestro.core))

(defn start []
  (mount/start-without #'maestro.core/repl-server))

(defn stop []
  (mount/stop-except #'maestro.core/repl-server))

(defn restart []
  (stop)
  (start))

