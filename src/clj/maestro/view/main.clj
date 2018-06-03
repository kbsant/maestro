(ns maestro.view.main
  (:require
    [maestro.form :as form]
    [maestro.view.parts :as parts]))

(defn content [{:keys [appname message]}]
  [:div.container
    (form/navbar [:h1 appname])
    (when message [:p message])
    [:div.container
      [:div.row
        [:div.col-sm.main-tile
           [:a {:href "/open"} [:img {:src "/img/file.png"}] [:br] [:p "Create or edit a survey"]]]
        [:div.col-sm.main-tile
          [:a {:href "/answer"} [:img {:src "img/entrance.png"}] [:br] [:p "Respond to a survey"]]]
        [:div.col-sm.main-tile
           [:a {:href "/result"} [:img {:src "img/calculator.png"}] [:br] [:p "Gather results"]]]]]])

(defn render [{:keys [glossary]}]
  (parts/main glossary 
    nil
    (content glossary)))

