(ns maestro.controller.message
  (:require [clojure.tools.logging :as log]
            [maestro.util.json :as json]
            [maestro.layout :as layout]
            [maestro.view.message :as message-view]
            [hiccup.util :as hiccup]
            [immutant.web.async :as async]))

(defn render
  [request]
  (layout/render-hiccup
    message-view/render
    {:glossary {:title "Messages"}}))

;; TODO manage this state -- make it reloadable
(defonce channels (atom {}))

(defn connect! [channel]
  (let [request (async/originating-request channel)
        session (:session request)]
    (log/info "channel open:" channel " session:" session)
    (swap! channels assoc channel session)))

(defn disconnect! [channel {:keys [code reason]}]
  (log/info "close code:" code " reason:" reason " channel:" channel " session:" (get @channels channel))
  (swap! channels dissoc channel))

(defn message-with-sender [data username]
  (let [in-msg (json/read-utf8 data)
        {:keys [text display-name]} in-msg
        sender (if display-name display-name username)
        out-msg {:text (hiccup/escape-html text)
                 :sender (hiccup/escape-html sender)}]
    (json/write-utf8 out-msg)))

(defn handle-data! [sender-channel data]
  (let [username (get-in @channels [sender-channel :username] "Guest")]
    (doseq [receiver-channel (keys @channels)]
      (let [receiver (get-in @channels [receiver-channel :username] "Guest")
            data-out (message-with-sender data username)]
        (log/info "notify sender:" username "receiver:" receiver " data:" data " data-out:" data-out)
        (async/send! receiver-channel data-out)))))

(def callbacks
  {:on-open connect!
   :on-close disconnect!
   :on-message handle-data!})

(defn ws-handler [request]
  (async/as-channel request callbacks))
