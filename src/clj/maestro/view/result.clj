(ns maestro.view.result
  (:require
    [maestro.view.parts :as parts]
    [maestro.view.doclist :as doclist]
    [hiccup.page :as page]))

(defn opener [data]
  (let [view-state (merge
                     (select-keys data [:glossary :flash-errors :doclist :open-link-base])
                     {:headline "Survey Results"})]
    (doclist/render view-state)))

(defn result-page [{:keys [survey-info flash-errors] :as data}]
  (parts/appbase
    data
    (parts/js-transit-var "transitState" data)
    (list
      (page/include-js "/js/app.js")
      [:script
        {:type "text/javascript"}
        "maestro.client.result.init();"])))

