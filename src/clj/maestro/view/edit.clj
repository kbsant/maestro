(ns maestro.view.edit
  (:require
    [maestro.view.parts :as parts]
    [maestro.view.doclist :as doclist]
    [hiccup.page :as page]))

(defn opener [data]
  (let [view-state (merge data {:headline "Survey Editor"})]
    (doclist/render view-state)))

(defn editor [data]
  (parts/appbase
    data
    (parts/js-transit-var
      "transitState"
      (select-keys
        data
        [:survey-info :response-link-base :export-link-base :flash-errors]))
    (list
      (page/include-js "/js/app.js")
      [:script
        {:type "text/javascript"}
        "maestro.client.edit.init();"])))



