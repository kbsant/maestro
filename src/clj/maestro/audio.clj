(ns maestro.audio
  (:require
    [mount.core :refer [defstate]])
  (:import (javax.sound.sampled AudioFormat AudioSystem DataLine DataLine$Info TargetDataLine)))
 
;; important - little endian (see last param: big endian false) is needed for wav compatibility
(def audio-format-16le (AudioFormat. 16000.0 16 1 true false))

(defn get-target[]
  (AudioSystem/getTargetDataLine audio-format-16le))

(defn open-target [target-mic]
  (doto target-mic .open .start))

(defn close-target [target-mic]
  (doto target-mic .stop .close))

(defn target-open? [target-mic]
  (.isOpen target-mic))

;; TODO mic capture should be done from the browser to make it more accessible
(defstate target-mic
  :start (open-target (get-target))
  :stop (close-target target-mic))

(defn read-target
  ([buf]
   (read-target target-mic buf))
  ([target-mic buf]
   (.read target-mic buf 0 (count buf))))


