(ns ^:figwheel-no-load maestro.app
  (:require
    [maestro.client.edit :as edit]
    [maestro.client.message :as message]
    [maestro.client.result :as result]
    [maestro.client.responder :as responder]
    [devtools.core :as devtools]))

(enable-console-print!)
(devtools/install!)
