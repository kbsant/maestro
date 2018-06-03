(ns maestro.client.websocket
  (:require [maestro.client.ui :as ui]))

(defonce ws-chan (atom nil))

(defn receive-transit-msg!
 [received-fn]
 (fn [msg]
   (received-fn
     (ui/read-json (.-data msg)))))

(defn close-channel!
  "Return a function to reset the channel to nil before passing control to closed-fn"
  [closed-fn]
  (fn [event]
    (do
      (reset! ws-chan nil)
      (closed-fn))))

;; TODO handle exceptions if sending when disconnected.
;; also, handle disconnection event by replacing ws atom with nil.
(defn send-transit-msg!
 [msg]
 (if-let [ch @ws-chan]
   (.send ch (ui/write-json msg))
   (throw (js/Error. "Websocket is not available!"))))

(defn make-websocket! [pathname {:keys [received-fn opened-fn closed-fn]}]
 (let [host (.-host js/location)
       proto (.-protocol js/location)
       wsprotocol (if (= -1 (.indexOf proto "s")) "ws://" "wss://")
       wsurl (str wsprotocol host pathname)]
   (if-let [chan (js/WebSocket. wsurl)]
     (do
       (set! (.-onmessage chan) (receive-transit-msg! received-fn))
       (set! (.-onopen chan) opened-fn)
       (set! (.-onclose chan) (close-channel! closed-fn))
       (println "Websocket connection established with: " wsurl)
       (reset! ws-chan chan))
     (throw (js/Error. "Websocket connection failed!")))))
