(ns gmnitron.commands.scene
  (:require [gmnitron.common :as common]
            [clojure.string :as str]
            [gmnitron.database :as database]))

(def no-scene-message "ERROR. ATTEMPTED TO ACCESS NON-EXISTENT SCENE. CREATE ONE WITH THE !establish COMMAND.")

(defn actor->display [actor]
  (str "**" (get actor :name) "**" (if (get actor :acted) " has acted this round." " hasn't acted this round.")))

(defn get-initiative-recap [scene]
    (let [initiative (group-by :acted (get scene :initiative))
          acted (str/join "\r\n" (map actor->display (get initiative true [])))
          unacted (str/join "\r\n" (map actor->display (get initiative false [])))]
      (str acted "\r\n\r\n" unacted)))

(defn get-scene-recap [scene]
  (let [{green :green-ticks yellow :yellow-ticks red :red-ticks tick :current-tick} scene
        boxes (concat (replicate green "Green") (replicate yellow "Yellow") (replicate red "Red"))]
    (if (>= tick (count boxes))
      "The scene has reached its end."
      (let [current-box (nth boxes (max 0 (- tick 1)))
            remaining-boxes (drop tick boxes)
            formatted-remaining-boxes (common/oxford (map #(str (second %) " " (first %) " boxes") (frequencies remaining-boxes)))]
        (common/fmt "It is currently a #{current-box} status. There are #{formatted-remaining-boxes} left.")))))

(defn recap [data]
  (let [{channel-id :channel-id} data]
    (if-let [scene (database/get-scene channel-id)]
      (str "\r\n***The Story so Far***\r\n\r\n" (get-scene-recap scene) "\r\n\r\n" (get-initiative-recap scene))
      no-scene-message)))

(defn establish [data]
  (let [{arguments :arguments channel-id :channel-id} data
        [green-ticks yellow-ticks red-ticks] (map common/str->int (take 3 arguments))
        names (drop 3 arguments)
        actors (map (fn [actor-name] {:name actor-name :search-name (str/lower-case actor-name) :acted false}) names)]
    (database/insert-scene channel-id {
      :green-ticks green-ticks
      :yellow-ticks yellow-ticks
      :red-ticks red-ticks
      :current-tick 0
      :initiative actors
    })
    (recap data)))

(defn pass [data]
  (let [{arguments :arguments channel-id :channel-id} data
        actor-name (first (get data :arguments))]
    (if (database/has-actor-in-scene channel-id actor-name)
      (do
        (database/update-scene-actor-acted channel-id actor-name true)
        (when (not (database/has-any-unacted-actors channel-id))
          (database/reset-scene-initiative channel-id))
        (recap data))
      no-scene-message)))

(defn tick [data]
  (let [{arguments :arguments channel-id :channel-id} data]
    (if-let [scene (database/get-scene channel-id)]
      (do
        (database/tick-scene channel-id)
        (recap data))
      no-scene-message)))

(def command-list [
  { :name "establish" :handler establish :min-args 3 :usage "!establish (number of green ticks) (number of yellow ticks) (number of red ticks) (actors)" }
  { :name "recap" :handler recap :max-args 0 :usage "!recap" }
  { :name "pass" :handler pass :min-args 1 :usage "!pass (actor name)" }
  { :name "tick" :handler tick :max-args 0 :usage "!tic" }
])