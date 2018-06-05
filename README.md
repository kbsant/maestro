# maestro
This is a Symphony speech to text bot developed during a hackathon.
It uses google cloud for speech to text conversion.

# Client
This uses the symphony client for clojure: <https://github.com/symphonyoss/clj-symphony>

# Usage

This requires java 8, clojure, leiningen, a google cloud account, and a symphony account.

Need to set the following environment variables:

* GOOGLE_APPLICATION_CREDENTIALS - the credentials file path for google cloud
* SYMPHONY_PARAMS_EDN - path of an EDN file containing the symphony client config, as explained in the clj-symphony and java symphony clients

Also, add the bot to the room which the text should be transcribed to.

This works primarily from the leiningen repl:

    lein repl  ;; start the repl
    (start)    ;; start the server




# Limitations
Speech capture is done on the server. For more flexibility, this is better done from the browser.
Recognition is done by sending data chunks. Better buffering/timing can reduce the breaks in audio. Streaming may also improve the quality.

# Information
* This software is a hobby project and not affiliated with Symphony, Google or any other company.
