(ns maestro.client.ui
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [clojure.core.async :as async :refer [>! <!]]
    [clojure.string :as string]
    [cognitect.transit :as t]))

(def json-reader (t/reader :json))

(def json-writer (t/writer :json))

(defn read-json [json]
  (t/read json-reader json))

(defn write-json [value]
  (t/write json-writer value))

(defn target-value [ev]
  (-> ev .-target .-value))

(defn js-assoc [key ev data]
  (assoc data key (target-value ev)))

(defn js-assoc-in [keys ev data]
  (assoc-in data keys (target-value ev)))

(defn js-update [key f ev data]
  (update data key f (target-value ev)))

(defn js-update-in [keys f ev data]
  (update-in data keys f (target-value ev)))

(defn js-assoc-fn [key ev]
  (partial js-assoc key ev))

(defn js-assoc-in-fn [key ev]
  (partial js-assoc-in key ev))

(defn js-update-fn [keys f ev]
  (partial js-update keys f ev))

(defn js-update-in-fn [keys f ev]
  (partial js-update-in keys f ev))

(defn element-by-id [id]
  (.getElementById js/document id))

(defn form-element-value [form name]
  (let [parent (-> form .-elements (.namedItem name))
        length (or (some-> (.-length parent) (> 1)) 0)
        parent-seq (array-seq parent)
        child-type (when (> length 0) (.-type (first parent-seq)))]
    (if (= "checkbox" child-type)
      (->> parent-seq (filter #(.-checked %)) (map #(.-value %)))
      (.-value parent))))

(defn fade-opacity [check-string]
   (if (string/blank? check-string)
      {:opacity 1}
      {:opacity 0 :transition [:opacity "3s"]}))

(defn repeat-timer [timer-fn millis]
  (go-loop []
    (<! (async/timeout millis))
    (timer-fn)
    (recur)))

(defn single-timer[timer-fn millis]
   (go
      (<! (async/timeout millis))
      (timer-fn)))

(defn log-in-out-button [state-info]
  (let [display-name (:display-name state-info)
        signed-in? (not (string/blank? display-name))
        button-text (if signed-in? "Sign out" "Sign in")
        nextpath-param (str "?nextpath=" (.-pathname js/location))
        button-action (if signed-in? "/signout" "/signin")]
    [:div
      [:span display-name]
      [:a.btn.btn-default.btn-xs
        {:role :button
         :href button-action}
        button-text]]))

(defn alert-through [s]
  (js/alert s)
  s)

(defn get-session [key]
  (.getItem (.-sessionStorage js/window) (name key)))

(defn set-session! [key value]
  (.setItem (.-sessionStorage js/window) (name key) value))

(defn get-local [key]
  (.getItem (.-localStorage js/window) (name key)))

(defn set-local! [key value]
  (.setItem (.-localStorage js/window) (name key) value))

(defn add-with-limit [max vec item]
  (let [new (conj vec item)
        extra (- (count new) max)]
    (if (> extra 0)
      (subvec new extra)
      new)))

(defn concat-with-limit [max orig items]
  (let [vec (if (vector? orig) orig (into [] orig))
        new (apply conj vec items)
        extra (- (count new) max)]
    (if (> extra 0)
      (subvec new extra)
      new)))

(defn inter-str-map
  "Given a format string and a map, replace every occurrence of each key with its
  corresponding value.
  'hello :a :b' {:a 'aa' :b 'bb'} -> 'hello aa bb'"
  [fmt arg-map]
  (reduce (fn [s [k v]] (string/replace  s (str k) (if (keyword? v) (name v) (str v)))) fmt arg-map))

(defn capital-first [s]
  (condp = (count s)
    0 s
    1 (string/upper-case s)
    (str  (string/upper-case (subs s 0 1)) (subs s 1))))

(defn prevent-default-stop-propagation [ev]
  (.preventDefault ev)
  (.stopPropagation ev)
  false)

