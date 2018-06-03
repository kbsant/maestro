(ns maestro.app
  (:require
    [maestro.client.edit :as edit]
    [maestro.client.message :as message]
    [maestro.client.result :as result]
    [maestro.client.responder :as responder]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))
