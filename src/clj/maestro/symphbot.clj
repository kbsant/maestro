(ns maestro.symphbot
  (:require
    [mount.core :refer [defstate]]
    [maestro.audio :as audio]
    [maestro.config :refer [env]]
    [maestro.speech :as speech]
    [clojure.data.xml :as xml]
    [clojure.core.async :as async :refer [>! <!]]
    [clj-symphony.connect :as syc]
    [clj-symphony.user :as syu]
    [clj-symphony.stream :as sys]
    [clj-symphony.chat :as sych]
    [clj-symphony.room :as syrm]
    [clj-symphony.message :as sym]
    [clj-symphony.user-connection :as syuc]))

(defn symphony-params []
  (->> (:symphony-params-edn env) slurp clojure.edn/read-string))

(defstate sy-client
  :start (syc/connect (symphony-params))
  :stop (syc/disconnect sy-client))

;; TODO bot control and language switching should be done by the bot/from the web
(def botstate (atom {:started nil
                     :count 100
                     :lang "en-US"}))

(defn room-streams [client]
  (->> client sys/streams (filter (comp #{:ROOM} :type)) (map :stream-id)))

;; use hiccup-style xml format
(defn msg-ml2[txt] (xml/emit-str (xml/sexp-as-element [:messageML txt])))

(defn  msg-ml [txt] (str "<messageML>" txt "</messageML>"))

(defn send-all-rooms!
  ([strs]
   (send-all-rooms! sy-client strs))
  ([client strs]
   (let [msg (msg-ml (if (seq? strs) (clojure.string/join " " strs) strs))
         streams (room-streams client)]
     (dorun (map #(sym/send-message! client % msg) streams)))))

;; TODO bot control and language switching should be done by the bot/from the web
;; TODO requesting recognition should be done asynchronously
(defn transcribe []
  (when (> (get @botstate :count 0) 0)
    (let [buf256k (byte-array (* 256 1024))
          _ (audio/read-target buf256k)
          lang (get @botstate :lang "en-US")
          response (speech/recognize-bytes lang buf256k)
          strs (speech/response-strs response)]
      (try (send-all-rooms! strs)
           (catch Exception e nil))
      (swap! botstate update :count dec)
      (recur))))    

;; TODO need finer async control -- recognition task should by async
(defn async-transcribe []
  (async/go (<! (transcribe))))

;; TODO this should  be controlled by the bot/from the web
(defn set-lang! [lang]
  (swap! botstate assoc :lang lang))

