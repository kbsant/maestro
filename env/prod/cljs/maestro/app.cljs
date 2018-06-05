(ns maestro.app
  (:require
    [maestro.client.message :as message]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))
