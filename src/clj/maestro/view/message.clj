(ns maestro.view.message
  (:require
    [maestro.view.parts :as parts]
    [hiccup.page :as page]))

(defn render [data]
  (parts/appbase
    data
    nil
    (list
      (page/include-js "/js/app.js") 
      [:script 
        {:type "text/javascript"}
        "maestro.client.message.init();"])))


