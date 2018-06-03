(ns maestro.view.error
  (:require
    [maestro.form :as form]
    [maestro.view.parts :as parts]))

(defn content [{:keys [status title message]}]
  [:div.container
    (form/navbar [:a {:href "/"} "Home"])
    [:h1 [:span.text-danger status]]
    [:h2 title]
    [:p [:span.text-danger message]]])

(defn render [{:keys [glossary]}]
  (parts/main glossary 
    nil
    (content glossary)))

