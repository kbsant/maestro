(ns maestro.audio
  (:import '(javax.sound.sampled AudioFormat AudioSystem DataLine DataLine$Info TargetDataLine)))
 
(def af1 (AudioFormat. 8000.0 16 1 false false)

(def target-mic (AudioSystem/getTargetDataLine af1))
(.start target-mic) 
(def bbc (byte-array 2048))
(.read target-mic bbc 0 1024)
(map int bbc)
(doto target-mic .stop .close)

(def bb (byte-array (* 1024 16)))

(def ssss (javax.sound.sampled.DataLine$Info. TargetDataLine af1)) 
(AudioSystem/isLineSupported ssss)
(.open llline af1 1024)
(.start llline)
(.read llline  bb 0 1024)
(map int bb)
(.stop llline)
(.close llline)
(.isOpen llline)
