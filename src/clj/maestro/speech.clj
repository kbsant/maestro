(ns maestro.speech
  (:require [mount.core :refer [defstate]])
  (:import (com.google.cloud.speech.v1p1beta1 RecognitionAudio
              RecognitionConfig RecognitionConfig$AudioEncoding
              RecognizeResponse SpeechClient SpeechRecognitionAlternative
              SpeechRecognitionResult)
           (java.nio.file Files Path Paths)
           com.google.protobuf.ByteString
           java.util.List))

(defn build-audio-cfg [lang]
  (.build
    (doto 
      (RecognitionConfig/newBuilder)
      (.setEncoding RecognitionConfig$AudioEncoding/LINEAR16)
      (.setSampleRateHertz 16000)
      (.setLanguageCode lang)
      (.setModel "default"))))

(defn audio-from-bytes [raw-audio]
  (let [audio-bytes (ByteString/copyFrom raw-audio)
        builder (doto (RecognitionAudio/newBuilder)
                      (.setContent audio-bytes))]
    (.build builder)))

(defstate speech-client
  :start (SpeechClient/create))

;; TODO language config should be cached in a map
(defstate audio-cfg-en
  :start (build-audio-cfg "en-US"))

(defstate audio-cfg-jp
  :start (build-audio-cfg "ja-JP"))

;; TODO use cached values from a lang-config map
(defn recognize-bytes [lang raw-audio]
  (let [audio-cfg (if (= lang "ja-JP") audio-cfg-jp audio-cfg-en)]
    (.recognize speech-client audio-cfg (audio-from-bytes raw-audio))))

(defn response-strs [response]
  (for [rl (.getResultsList response) 
        :let [al (.getAlternativesList rl)]]
    (some-> (first al) .getTranscript)))


