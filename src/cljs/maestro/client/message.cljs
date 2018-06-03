(ns maestro.client.message
  (:require [reagent.core :as r]
            [maestro.client.websocket :as wsclient]
            [maestro.client.ui :as ui]
            [ajax.core :refer [GET POST]]))

(defonce state (r/atom
                {:conn-state :closed   ; values are :open :connecting :closed
                 :messages []
                 :display-name ""}))

(defn update-messages! [message]
  (swap! state update :messages #(ui/add-with-limit 10 % message))
  (ui/set-local! :messages (ui/write-json (:messages @state))))

(defn message-list []
 [:ul
  (for [[i message] (map-indexed vector (:messages @state))]
    (let [{:keys [sender text]} message]
      ^{:key i}
      [:li
        [:span {:style {:padding-right 10 :font-weight :bold}}
          [:strong sender]]
        text]))])

(declare init-websocket)

(defn reconnect-button []
  (let [closed (= :closed (:conn-state @state))
        btn-visibility (if closed :visible :hidden)
        btn-display (if closed :inline :none)
        btn-action (if closed init-websocket (constantly 0))]
     [:input
      {:type :button
       :value "Reconnect"
       :on-click btn-action
       :style {:visibility btn-visibility
               :display btn-display}}]))

(defn connection-status-text []
  (let [conn-state (:conn-state @state)
        text-state (get {:closed "Disconnected."
                         :connecting "Connecting..."
                         :open "Connected."}
                    conn-state)
        style (if (= :open conn-state)
                  {:opacity 0 :transition [:opacity "3s"]}
                  {:opacity 1})]
    [:p.small {:style style}
      text-state]))

(defn update-display-name! [event]
  (let [display-name (-> event .-target .-value)]
    (swap! state assoc :display-name display-name)
    (ui/set-local! :display-name display-name)))

(defn display-name-input []
  (fn []
    [:input.form-control
     {:type :text
      :placeholder "alias"
      :value (:display-name @state)
      :on-change update-display-name!}]))


;; TODO set max message size
;; TODO auto-reconnect on message input
(defn message-input []
 (let [value (r/atom "")]
   (fn []
     [:input.form-control
      {:type :text
       :placeholder "type in a message and press enter"
       :value @value
       :on-change #(reset! value (-> % .-target .-value))
       :on-key-down
       #(when (= (.-keyCode %) 13)
          (when-not (clojure.string/blank? @value)
            (wsclient/send-transit-msg!
              {:text @value :display-name (:display-name @state)}))
          (reset! value ""))}])))

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Open Messaging"]]
   [:div.container
    [:div.row
     [:div.col-sm-6
      [message-list]]]
    [:div.row
     [:div.col-sm-6
      [reconnect-button]]]
    [:div.row
     [:div.col-sm-2
      [display-name-input]]
     [:div.col-sm-6
        [message-input]]]
    [:div.row
        [connection-status-text]]]])

(defn mount-components []
  (r/render [home-page] (.getElementById js/document "app")))

(defn load-messages []
  (when-let [json-messages (ui/get-local :messages)]
    (swap! state assoc :messages (ui/read-json json-messages))))

(defn fetch-name []
  (if-let [display-name (ui/get-local :display-name)]
    (swap! state assoc :display-name display-name)
    (GET "/displayname" {:handler #(swap! state assoc :display-name %)})))

(defn set-conn-state-fn [conn-state]
  (fn [] (swap! state assoc :conn-state conn-state)))

(defn set-conn-state! [status]
  ((set-conn-state-fn status)))

(defn init-websocket []
  (set-conn-state! :connecting)
  (wsclient/make-websocket! "/ws/message"
    {:received-fn update-messages!
     :opened-fn (set-conn-state-fn :open)
     :closed-fn (set-conn-state-fn :closed)}))

(defn ^:export init []
  (mount-components)
  (fetch-name)
  (load-messages)
  (init-websocket))
